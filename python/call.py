import evaluate
import os # to read in and process data
import joblib # to pickle
import tensorflow as tf


### Load action_encoder
folder_path = "../data"

files = [f for f in os.listdir(folder_path)
         if os.path.isfile(os.path.join(folder_path, f))
         and f.startswith('processed')]
file = files[-1] # take the last file that has been processed

### Load data
_, _, action_encoder = joblib.load(folder_path + '/' + file)
model = tf.keras.models.load_model("../data/action_quality_model.keras")

### Load data from Game
files = [f for f in os.listdir(folder_path)
         if os.path.isfile(os.path.join(folder_path, f))
         and f.startswith('data')]
file = files[0] # take the last file that has been processed

with open(folder_path + "/" + file, "r") as f:
    data = eval(f.read())

    # Create label: If player d[2][0] is the winner d[2][1], then label is set to true.
    for d in data:
        d[2] = d[2][0] == d[2][1]

all_possible_actions = list(range(16))

for d in data:
    gameState, action, label = d
    res = evaluate.pick_best_action(model, gameState, all_possible_actions, action_encoder)
    print(res)


