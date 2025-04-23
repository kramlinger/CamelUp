

import os # to read in and process data
import joblib # to pickle
import tensorflow as tf

# Import data
folder_path = "../data"

files = [f for f in os.listdir(folder_path)
         if os.path.isfile(os.path.join(folder_path, f))
         and f.startswith('processed')]
file = files[-1] # take the last file that has been processed

### Load data
X, y, label_encoder = joblib.load(folder_path + '/' + file)


### Setup model
input_dim = X.shape[1]

model = tf.keras.models.Sequential([
    tf.keras.layers.Input(shape=(input_dim,)),
    tf.keras.layers.Dense(64, activation='relu'),
    tf.keras.layers.Dense(64, activation='relu'),
    tf.keras.layers.Dense(1, activation='sigmoid')  # Output is probability that (x, a) is a "good" move
])

model.compile(optimizer='adam',
              loss='binary_crossentropy',
              metrics=['accuracy'])

### Fit model
model.fit(X, y, epochs=10, batch_size=32, validation_split=0.1)

model.save("../data/action_quality_model.keras")