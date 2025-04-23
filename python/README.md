### How to update the network

0. Generate game data by running `HeadlessMode.java` with specified size and players. Data is saved at `data/data[...].txt`.
1. Run `preprocessing.py` to organize the data for the multiclassification setting used. 
2. Run `fitting.py` to fit the NN. The fitted model is saved as `data/action_quality_model.keras`
3. Now, `model.py` is called via Java and `data/action_quality_model.keras` used to evaluate the model.  
