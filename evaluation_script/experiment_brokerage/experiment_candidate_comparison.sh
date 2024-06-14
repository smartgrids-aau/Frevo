#!/bin/bash

#!/bin/bash
# script to run Frevo

if [ "$USER" == "andreamonacchi" ]; then
	path="/Users/andreamonacchi/Documents/workspace/"
else
	path="/home/sci/amonacch/simulations/"
fi

# construct the java class path
export CLASSPATH=$(echo "
${path}FREVO/Components/Problems/Max_Traveler:
${path}FREVO/Components/Problems/SimplifiedEmergencyExit:
${path}FREVO/Components/Representations/MealyFSM:
${path}FREVO/Components/Representations/NEAT:
${path}FREVO/Components/Problems/CAM:
${path}FREVO/Components/Problems/cDrones:
${path}FREVO/Components/Problems/dDrones:
${path}FREVO/Components/Problems/EmergencyExit:
${path}FREVO/Components/Problems/EvoPCO:
${path}FREVO/Components/Problems/EvoSynch:
${path}FREVO/Components/Problems/InvertedPendulum:
${path}FREVO/Components/Problems/Light:
${path}FREVO/Components/Problems/nnracer:
${path}FREVO/Components/Problems/Patterns:
${path}FREVO/Components/Problems/RobotSoccer:
${path}FREVO/Components/Problems/simsoccer:
${path}FREVO/Components/Problems/SmartGrid:
${path}FREVO/Components/Problems/ttt2:
${path}FREVO/Components/Problems/ttt3:
${path}FREVO/Components/Problems/XOR:
${path}FREVO/Components/Methods/CEA2D:
${path}FREVO/Components/Methods/CuckooSearch:
${path}FREVO/Components/Methods/gaspecies:
${path}FREVO/Components/Methods/nnga:
${path}FREVO/Components/Methods/NoveltySearch:
${path}FREVO/Components/Methods/NoveltySpecies:
${path}FREVO/Components/Methods/RandomSearch:
${path}FREVO/Components/Representations/CompleteNetwork:
${path}FREVO/Components/Representations/FullyMeshedNet:
${path}FREVO/Components/Problems/SimpleSoccer:
${path}FREVO/Components/Representations/HebbNet:
${path}FREVO/Components/Representations/LayeredNetwork:
${path}FREVO/Components/Representations/SimpleBulkRepresentation:
${path}FREVO/Components/Rankings/AbsoluteRanking:
${path}FREVO/Components/Rankings/FullTournament:
${path}FREVO/Components/Rankings/MultiSort:
${path}FREVO/Components/Rankings/MultiSwiss:
${path}FREVO/Components/Rankings/NoveltyRanking:
${path}FREVO/Components/Rankings/SwissSystem:
${path}FREVO/Components/Problems/CIPI:
${path}FREVO/Components/Methods/PSO:
${path}FREVO/Components/Representations/DebuggingRepresentation:
${path}FREVO/Components/Problems/SinglePong:
${path}FREVO/Components/Problems/HEMS:
${path}FREVO/Components/Problems/EnergyBrokers:
${path}FREVO/Libraries/JProwler/jprowler_v1_0.jar:
${path}FREVO/Libraries/jafama/jodk.jar:
${path}FREVO/Libraries/jgraphx/jgraphx.jar:
${path}FREVO/Libraries/JFreeChart/jfreechart-1.0.17.jar:
${path}FREVO/Libraries/JFreeChart/jcommon-1.0.21.jar:
${path}FREVO/Libraries/resources.jar:
${path}FREVO/Libraries/JGridMap/JGridMap.jar:
${path}FREVO/Libraries/jchart2d-3.2.2/jchart2d-3.2.2.jar:
${path}FREVO/Libraries/dom4j/dom4j-1.6.1.jar:
${path}FREVO/Libraries/jlfgr/jlfgr-1_0.jar:
${path}FREVO/Libraries/macify/macify-1.4.jar:
${path}FREVO/Libraries/WhiteBoard/WhiteBoard.jar:
${path}FREVO/Libraries/jaxen/jaxen-1.1.4.jar:
${path}FREVO/Libraries/SimpleJSON/json-simple-1.1.1.jar:
${path}FREVO/Libraries/Jung2/jung-algorithms-2.0.1.jar:
${path}FREVO/Libraries/Jung2/jung-api-2.0.1.jar:
${path}FREVO/Libraries/Jung2/jung-graph-impl-2.0.1.jar:
${path}FREVO/Libraries/Jung2/jung-io-2.0.1.jar:
${path}FREVO/Libraries/Jung2/jung-jai-2.0.1.jar:
${path}FREVO/Libraries/Jung2/stax-api-1.0.1.jar:
${path}FREVO/Libraries/Jung2/vecmath-1.3.1.jar:
${path}FREVO/Libraries/Jung2/wstx-asl-3.2.6.jar:
${path}FREVO/bin:"  | sed "s/ *//g" | tr -d '\n') 
java -Xmx10g brokerage.Market $*


mail -s "Execution of the script on $(hostname) is done!" andrea.monacchi@aau.at