from flask import Flask, request, jsonify
import evaluate
import os
import joblib
import tensorflow as tf
import numpy as np

# Load encoder and model once when the server starts
folder_path = "./data"
files = [f for f in os.listdir(folder_path)
         if os.path.isfile(os.path.join(folder_path, f))
         and f.startswith('processed')]
file = files[-1]

_, _, action_encoder = joblib.load(folder_path + '/' + file)
model = tf.keras.models.load_model("./data/action_quality_model.keras")

all_possible_actions = list(range(16))

app = Flask(__name__)

@app.route("/ping", methods=["GET"])
def ping():
    return "pong", 200


@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json()
    if "gameState" not in data:
        return jsonify({"error": "Missing 'gameState' in request"}), 400

    try:
        gameState = list(map(int, data["gameState"]))
    except Exception as e:
        return jsonify({"error": f"Invalid input: {e}"}), 400

    # Evaluate actions
    move = [
        evaluate.evaluate_action(model, gameState, a, action_encoder)
        for a in all_possible_actions
    ]

    return jsonify({"result": [float(x) for x in move]})


if __name__ == "__main__":
    app.run(port=5050)


