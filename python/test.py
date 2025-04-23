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
