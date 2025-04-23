###
###    preprocessing.py
###
###    Reads data from "../data/data[...].txt", brings it in x, action, label format and
###    dumps it as .joblib file in the same directory.
###

import os # to read in and process data
import joblib # to pickle
import numpy as np
from sklearn.preprocessing import LabelEncoder # to encode

# Read in and process data
folder_path = "../data"

files = [f for f in os.listdir(folder_path)
         if os.path.isfile(os.path.join(folder_path, f))
         and f.startswith('data')]

print(len(files))

data = [[] for _ in files]

winner = []

for i, file in enumerate(files):
    with open(folder_path + "/" + file, "r") as f:
        data[i] = eval(f.read())

        # Create label: If player d[2][0] is the winner d[2][1], then label is set to true.
        for d in data[i]:
            d[2] = d[2][0] == d[2][1]

data = [d for dat in data for d in dat]



# Split into components
X_raw = []
y = []

action_encoder = LabelEncoder()

# Fit action encoder on all actions
all_actions = [a for _, a, _ in data]
action_encoder.fit(all_actions)

for x_vec, action, label in data:
    action_encoded = action_encoder.transform([action])[0]
    combined_input = x_vec + action_encoded  # concatenate action to state
    X_raw.append(combined_input)
    y.append(int(label))  # True -> 1, False -> 0

X = np.array(X_raw, dtype=np.float32)
y = np.array(y, dtype=np.float32)

# Save
name = "../data/processed_" + files[0] + "_to_" + files[-1]
joblib.dump((X, y, action_encoder), name + ".joblib")