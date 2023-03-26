for i in $(seq 1 5000)
do
echo -e "\nRun n.o " $i
java MuseumHeist out.txt
done
