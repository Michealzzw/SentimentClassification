#!/bin/bash
cd libsvm-3.21
cd ../bin
java ClassificationAsSimilarity.NearestReplaceSVMCalulateK
java DataProcess.MergeSVM2FinalAns
cd ..
cd eval_semeval16_task6_v2/
echo Atheism
perl eval_topic.pl ../Atheism_gold.txt ../Atheism.txt
echo
echo Climate
perl eval_topic.pl ../Climate_Change_is_a_Real_Concern_gold.txt ../Climate_Change_is_a_Real_Concern.txt
echo Feminist
echo
perl eval_topic.pl ../Feminist_Movement_gold.txt ../Feminist_Movement.txt
echo Hillary
echo
perl eval_topic.pl ../Hillary_Clinton_gold.txt ../Hillary_Clinton.txt
echo Abortion
echo
perl eval_topic.pl ../Legalization_of_Abortion_gold.txt ../Legalization_of_Abortion.txt
echo All
perl eval_topic.pl ../SemEval2016-Task6-subtaskA-testdata-gold.txt ../FinalAns.txt 
echo $i