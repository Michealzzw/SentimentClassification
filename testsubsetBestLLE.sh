#!/bin/bash
cd bin
java DataProcess.SVMSubsetSelection
cd ..
cd libsvm-3.21
for i in {1..5}; do ./svm-train -c 8192.0 -g 8 ../ans_SVM_train_$i.txt ; done



#SVM 8.0 0.0001220703125
#	2.0 0.00048828125
#	8.0 0.0001220703125
#	8.0 0.0001220703125
#	512.0 3.0517578125e-05
#	8.0 0.001953125



#PCA-3  512.0 0.0001220703125
#	32 0.0125 61.3383
#	512.0 0.0001220703125 75.0
#	512.0 0.0001220703125 58.0357
#	2048.0 3.0517578125e-05 74.2857a
#	0.03125 0.03125 63.3333


#LE-9 32768.0 8.0 61.1524
#	2048.0 8.0 78.5354
#	32768.0 2.0 78.0127
#	32768.0 8.0 79.4989
#	512.0 0.5 94.7137




#./svm-train -c 8 -g 2 ../ans_SVM_train_1.txt
#./svm-train -c 0.5 -g 2 ../ans_SVM_train_2.txt
#./svm-train -c 2 -g 8 ../ans_SVM_train_3.txt
#./svm-train -c 8 -g 8 ../ans_SVM_train_4.txt
#./svm-train -c 0.125 -g 0.5 ../ans_SVM_train_5.txt
for i in {1..5}; do ./svm-predict ../ans_SVM_test_$i.txt ans_SVM_train_$i.txt.model $i.ans; done
cd ../bin
java DataProcess.MergeSVM2FinalAns
cd ..
cd eval_semeval16_task6_v2/
#echo Atheism
perl eval_topic.pl ../Atheism_gold.txt ../Atheism.txt
#echo Climate
perl eval_topic.pl ../Climate_Change_is_a_Real_Concern_gold.txt ../Climate_Change_is_a_Real_Concern.txt
#echo Feminist
perl eval_topic.pl ../Feminist_Movement_gold.txt ../Feminist_Movement.txt
#echo Hillary
perl eval_topic.pl ../Hillary_Clinton_gold.txt ../Hillary_Clinton.txt
#echo Abortion
perl eval_topic.pl ../Legalization_of_Abortion_gold.txt ../Legalization_of_Abortion.txt
perl eval_each_topic.pl ../Atheism_gold.txt ../Atheism.txt ../Climate_Change_is_a_Real_Concern_gold.txt ../Climate_Change_is_a_Real_Concern.txt ../Feminist_Movement_gold.txt ../Feminist_Movement.txt ../Hillary_Clinton_gold.txt ../Hillary_Clinton.txt ../Legalization_of_Abortion_gold.txt ../Legalization_of_Abortion.txt
perl eval_topic.pl ../SemEval2016-Task6-subtaskA-testdata-gold-origin.txt ../FinalAns.txt 
echo 
