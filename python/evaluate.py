import numpy as np

def evaluate_action(model, state_vector, action_str, action_encoder):
    action_id = action_encoder.transform([action_str])[0]
    input_vector = np.array(state_vector + action_id, dtype=np.float32).reshape(1, -1)
    prob = model.predict(input_vector, verbose=0)[0][0]
    return prob  # probability this action is good

def pick_best_action(model, state_vector, all_possible_actions, action_encoder):
    best_score = -1
    best_action = None
    for action in all_possible_actions:
        score = evaluate_action(model, state_vector, action, action_encoder)
        if score > best_score:
            best_score = score
            best_action = action
    return best_action
