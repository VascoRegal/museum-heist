for i in $(seq 1 2000)
do
echo -e "\nRun n.o " $i
java MuseumHeist outs/out$i.txt
done
