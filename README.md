### CamelUp!

This repository contains an implementation of the board game *CamelUp*.  
If you're unfamiliar with the game, check out the rulebooks in `./pdf`.

To start, I added two basic AI players: `Random`, which picks actions randomly,  
and `Greedy`, which chooses the action with the highest expected payoff.  
The game has a surprising number of possible outcomes — with `n = 5` camels,  
each able to move up to `k = 3` spaces, the total number of possible move outcomes  
is `k ** n * factorial(n) = 29,160`. Because of this complexity, I used a simplified  
version of `Greedy`. For instance, it will only bet on a race winner if a camel  
could win on the next move.

Using these two AIs, I simulated `10,000` games, resulting in `741,626` AI decisions.  
This data was used to train a simple neural network using `TensorFlow`.  
The resulting player, `NN`, makes decisions based on the trained model.

The model is saved as `./data/action_quality_model.keras` and is accessed via Python.  
To unify decision-making, I set up a Flask server that handles all communication  
between Java and Python.

Now that `NN` is fully integrated, you can generate more training data by having  
it play against `Greedy`, or explore more sophisticated learning methods for the network.
