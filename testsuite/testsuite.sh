#!/bin/bash
#
# testsuite.sh
#
# author: Thomas Dittrich
#
# This script is a test script for Frevo. It uses the testcases (Frevo Session
# Files) in the Folder /Frevo/testsuite/testcases to check if any components
# of Frevo have changed. Therefor it creates a reference hash file for every
# testcase. This hash file is created when the testcase runs for the first time
# or the command -renew is passed as an argument. In the end of the test the
# script lists all the testcases in groups of testcases that have passed the
# test, testcases With a new hash file, testcases that didn't pass the test and
# testcases that contain an error (i.e. Frevo produces no result file)
#
#
# usage: ./testsuite [-renew] [testcase-files] 
#

passedsessions=""
changedsessions=""
newfiles=""
errorsessions=""
renew=""
#Check for args
while [ $# -gt 0 ]
do
	case "$1" in
		-renew) renew="-renew";;
		-*) echo -e "Unknown Command: $1\nAvailable Commands: -renew";;
		*) break;;
	esac
	shift
done
#If files have been passes as args use those else use all testcases
files=$@
if [ "$files" = "" ]
then
	files=`ls testcases/*.zse`
fi
echo $renew
echo -e $files
for f in $files
do
	#Remove all result files from the /Results/Test folder
	rm -f ../Results/Test/*
	#Get the filename without directory and extension
	filename=$(basename $f)
	filename=${filename%%.*}
	#File that contains the reference hash
	hashfile=$filename.hash
	#Launch Frevo
	time ../launch_Frevo.sh -s testcases/$filename.zse >/dev/null 2>&1
	#Get Frevo-Result-File
	filetocompare=`ls -t -r ../Results/Test/*.zre | tail -n 1`
	if [[ $filetocompare == "" ]]
	then
		echo -e "\e[1;31mError in $filename\e[0m"
		#List all sessions that have passed the test
		errorsessions="$errorsessions\n$filename"
	else
		#Calculate md5sum
		hash=`md5sum "$filetocompare"`
		#Convert hash to string (maybe not needed)
		hashstring=`echo $hash`
		#If reference hashfile is abailable and -renew has not been passed as arg
		if [[ -f $hashfile ]] && [[ "$renew" != "-renew" ]]
		then
			echo "Comparing hash of $filetocompare"
			echo ${hashstring}
			#Get reference-hash
			refhash=`cat $hashfile`
			echo "with reference hash ($hashfile)"
			echo $refhash
			#Compare hash
			if [[ "`echo ${refhash}`" == "`echo ${hashstring}`" ]]
			then
				echo -e "\e[1;32mTest passed\e[0m"
				#List all sessions that have passed the test
				passedsessions="$passedsessions\n$filename"
			else
				echo -e "\e[1;31mSomething has changed\e[0m"
				#List all sessions whose hash has changed
				changedsessions="$changedsessions\n$filename"
			fi
		else
			echo -e "\e[1;34mCreating reference hash file ($hashfile) for $f with hash: $hashstring\e[0m"
			#List all reference hash files that have been created
			newfiles="$newfiles\n$hashfile"
			#Create reference hash file
			touch $hashfile
			#Write hash to file
			echo ${hashstring} > $hashfile
		fi
	fi
done
echo -e "\nSessions that have passed the test:"
echo -e "\e[1;32m$passedsessions\e[0m"
echo -e "\nNew hashfiles:"
echo -e "\e[1;34m$newfiles\e[0m"
echo -e "\nSessions that have changed:"
echo -e "\e[1;31m$changedsessions\e[0m"
echo -e "\nSessions that contain an error:"
echo -e "\e[1;31m$errorsessions\e[0m"
