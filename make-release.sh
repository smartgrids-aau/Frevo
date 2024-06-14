#!/bin/bash
# script to create a tarball release of Frevo

#COMPONENTS="XOR.XOR SSEA2D.SSEA2D InvertedPendulum.InvertedPendulum components.simplesoccer.SimpleSoccer ttt2.ttt2 nnga.NNGA threeLayerNetwork.ThreeLayerNetwork fsm.MealyFSM fullyMeshedNet.FullyMeshedNet swissSystem.SwissSystem AbsoluteRanking.AbsoluteRanking fulltournament.FullTournament"
COMPONENTS="singlePong.SinglePong pong.Pong hems.Market AbsoluteRanking.AbsoluteRanking cam.Cam CEA2D.CEA2D components.cdrones.cdrones components.ddrones.ddrones components.simplesoccer.SimpleSoccer fehervari.noveltyranking.NoveltyRanking fehervari.noveltysearch.NoveltySearch fehervari.noveltyspecies.NoveltySpecies frevo.method.randomsearch.RandomSearch fulltournament.FullTournament fullyMeshedNet.FullyMeshedNet gaspecies.GASpecies hebbNet.HebbNet InvertedPendulum.InvertedPendulum light.Light MultiSort.MultiSort MultiSwiss.MultiSwiss neat.NEAT nnga.NNGA nnracer.NNRacer swissSystem.SwissSystem threeLayerNetwork.ThreeLayerNetwork ttt2.ttt2 XOR.XOR"
echo "The following components will be kept in the release:\n$COMPONENTS"

# the following files in the Frevo top level directiry will be copied
files2copy="license.txt .classpath .project build.sh build.xml readme.txt createscripts.jar"

basedir=$PWD
tmpdir="frevo-release-tmp"
releasedir="$tmpdir/Frevo"

echo "building binaries"
ant
echo "copying files"
rm -rf $releasedir
mkdir -p $releasedir
cp -r Components $releasedir
cp -r Libraries $releasedir
cp -r src $releasedir
cp -r bin $releasedir
cp javadoc.xml license.txt .classpath .project build.xml readme.txt createscripts.jar $releasedir 
cd $releasedir
echo "removing .svn directories"
find . -name ".svn" -exec rm -rf {} \; 2>/dev/null 

echo "creating temporary start scripts"
java -jar createscripts.jar
echo "create list of installed components"
INSTALLED=`./launch_Frevo.sh -l| tr '(' '\n'|grep ")"| tr ')' ' '|grep -v ":" | tr '\n' ' '`

# remove every component that is not listed in $COMPONENTS
for c in $INSTALLED
do
    keep=False
    for i in $COMPONENTS
    do
       if [ "$i" = "$c" ]  
       then
           keep=True 
       fi
    done
    if [ $keep = False ]
    then
        echo "Removing $c"
	./launch_Frevo.sh -r "$c"
    fi
done

echo "generating Javadoc"
ant -f javadoc.xml

VERSION=`grep "MAJORVERSION = " src/main/FrevoMain.java |cut -d\" -f2`
echo $VERSION
archivename="Frevo_v${VERSION}.zip"
archivedir="$basedir/Release/${VERSION}"

# remove path-dependent launch scripts 
rm -rf launch_Frevo.*
cd ..
echo $PWD
ls
echo "creating archive $archivedir/$archivename"
mkdir -p "$archivedir"
rm -rf "$archivedir/$archivename"
zip -r "$archivedir/$archivename" Frevo >ziplog.log #>/dev/null
if [ $? != 0 ]
then
    echo "Error in zip operation"
else
    echo "zipping successful"
fi

echo "done."
