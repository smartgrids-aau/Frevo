problem="Pong"
# define the number of parallel threads	
parallelthreads=8
popsize=81
generations=500
maxPaddleSize=60
steps=2000

# run for different evolutionary algorithms
for algorithm in "NNGA" # "RandomSearch" "CEA2D"
do
  # run for different neural networks
  for nn in "FMN" # "3LN"
  do
    # the name of the file with defined settings to start experiment
    template="${problem}_${algorithm}_${nn}_template.zse"
        
    for players in {1..5} # "3LN"
    do
	
      for balls in {1..6}
      do
	
        for seed in {1234..1235}
        do
          # define the name of the folder where to keep the files with information about the experiments
          dir_res="${problem}_${algorithm}_${nn}_p${players}_b${balls}_s${seed}"

	  # decreasing of paddle size while the number of players increses
	  paddle_size=`expr ${maxPaddleSize} / ${players}`
				
          # create the session file
          sed -e "s/-STEPS-/${steps}/g" -e "s/-PADDLE_SIZE-/${paddle_size}/g" -e "s/-BALLS-/${balls}/g"  -e "s/-PLAYERS-/${players}/g" -e "s/-SEED-/${seed}/g" -e "s/-GENERATIONS-/${generations}/g" -e "s/-POPSIZE-/${popsize}/g" -e "s/-PARALLELTHREADS-/${parallelthreads}/g" -e "s/-INFO-/${dir_res}/g" < ${template} > session.zse

          # run frevo
          ../launch_Frevo.sh -s session.zse  
        done
      done
    done
  done
done

echo "Execution of the $(problem) problem on $(hostname) is done!" | mail -s "Execution of the $(problem) problem on $(hostname) is done!" sergii.zhevzhyk@aau.at

