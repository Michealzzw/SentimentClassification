tol = 1e-8;
d = 10;
figure(4);
fVa=fopen('st_Matrix_Value.txt','r');
fID=fopen('st_Matrix_ID.txt','r');
Yall = load('ans.txt');
mark = 0;
for topic = 1:5
    tline = fgetl(fID);
    tline = fgetl(fVa);
    tline = str2num(tline);
    dnum = tline(1)
    topK = tline(2)
    modelNum = tline(3)
    Ymodel = Yall(1+mark:mark+modelNum,:);
    mark = mark + modelNum;
    Y = zeros(dnum,d);
    %W = zeros(dnum,topK);
    Neighbour = zeros(dnum,topK);
    for docu = 1:dnum
        Matrix = [];
        tline = fgetl(fID);
        tline = str2num(tline);
        Neighbour(docu,:) = tline+1;
        for i = 1:topK
           tline = fgetl(fVa);
           tline = str2num(tline);
           Matrix = [Matrix;tline];
        end;
        Matrix = Matrix + eye(topK,topK)*tol*trace(Matrix);
        ansallo = Matrix\ones(topK,1);
        ansallo = ansallo / sum(ansallo);
%        fprintf(fw,'%g\t',ans);
%        fprintf(fw,'\n');
        %W(docu,:) = ansallo;
        Y(docu,:) = Ymodel(Neighbour(docu,:),:)'*ansallo;
    end
    Y = Y';
    tline = fgetl(fVa);
    tline = str2num(tline);
    Label = tline;
    FavorL = [];
    AgainstL = [];
    NoneL = [];
    for i=1:dnum
        if (Label(i)==1) FavorL = [FavorL,i];end
        if (Label(i)==0) NoneL = [NoneL,i];end
        if (Label(i)==-1) AgainstL = [AgainstL,i];end
    end
    out_file_name = strcat('ans_SVM_train_',strcat(num2str(topic),'.txt'));
    fw=fopen(out_file_name,'w');
    %fprintf(fw,'%d\t%d\n',dnum,modelNum);
    for i=1:dnum
        fprintf(fw,'%d\t',Label(i));
        for j=1:d
            fprintf(fw,'%d:%f\t',j,Y(j,i));
        end
        fprintf(fw,'\n');
                if (i==modelNum)
            fclose(fw);
            out_file_name = strcat('ans_SVM_test_',strcat(num2str(topic),'.txt'));
            fw=fopen(out_file_name,'w');
        end
    end
    
    %figure(topic)
    %
    subplot(1,5,topic);
    hold on    
    scatter3(Y(1,FavorL),Y(2,FavorL),Y(3,FavorL),'g')
    hold on    
    scatter3(Y(1,AgainstL),Y(2,AgainstL),Y(3,AgainstL),'r')
    hold on    
    scatter3(Y(1,NoneL),Y(2,NoneL),Y(3,NoneL),'k')
    fclose(fw);
end

fclose(fID);
fclose(fVa);
