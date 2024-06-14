
                               F   R   E   V   O
			Framework for evolutionary design

  What is it? 
  -----------
  
  FREVO is an open-source framework developed in Java to help engineers and scientists in evolutionary design or optimization tasks. The major feature of FREVO is the componentwise decomposition and separation of the key building blocks for each optimization tasks. We identify these as the problem definition, solution representation and the optimization method. This structure enables the components to be designed separately allowing the user to easily swap and evaluate different configurations and methods or to connect an external simulation tool.
  
  This release contains the following components:
  
  Problems:
		XOR: The well-known XOR function to be evolved.
		InvertedPendulum: A problem for controlling a small cart to balance an inverted pendulum.
		ttt2: Tic Tac Toe on a 2x2 board.
		SimpleSoccer: A simplified self-organized robot soccer simulation.
		
  Methods:
		NNGA: A simple genetic algorithm that supports multiple populations and ranking algorithms
		SSEA2D: A 2-dimensional genetic algorithm where mutation and recombination operators only effect 	neighboring candidates.
		
  Representations:
		FullyMeshedNet: A fully-connected, recurrent, time-discrete, artificial neural network.
		LayeredNetwork: Feedforward artificial neural network with one hidden layer.
		MealyFSM: Mealy-machine FSM implementation.
  Ranking:
		AbsoluteRanking: A simple ranking that sorts candidates with single fitness.
		FullTournament: A round-robin tournament meant for problems that can only be evaluated relatively.
		SwissSystem: An optimized tournament with only Log2N rounds.
  
  For more information, please refer to http://sourceforge.net/p/frevo

  Installation
  ---------

  FREVO requires the Java environment (version min. 1.6) to be installed. Type java -version in the console to see if you have the compatible version installed.
  
  To simply start FREVO with its GUI, follow these steps:
  -------------------------------------------------------
  
  1. Start a console window and type
		java -jar createscripts.jar
	This will create the script files used to start FREVO.
  2. Based on your operating system, run the following file:
	Windows: launch_Frevo.bat
	Unix/Linux/Mac OSX: ./launch_Frevo.sh
	
  Developing with FREVO:
  ----------------------
  
  FREVO is primarily developed using Eclipse (www.eclipse.org).
  The Eclipse project file can be found in the root directory. Use "import existing project into workspace" to import FREVO into your Eclipse workspace.

  Licensing
  ---------

  This software is licensed as open source under the Apache License 2.0. Please find further information in license.txt

