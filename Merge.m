tol = 1e-5;
d = 10;
figure(3);
fVa=fopen('Matrix_Value.txt','r');
fID=fopen('Matrix_ID.txt','r');
fw=fopen('ans.txt','w');
for topic = 1:5
    tline = fgetl(fID);
    tline = fgetl(fVa);
    tline = str2num(tline);
    dnum = tline(1)
    topK = tline(2)   
    W = zeros(dnum,topK);
    Neighbour = zeros(dnum,topK);
    for docu = 1:dnum
        Matrix = [];
        tline = fgetl(fID);
        tline = str2num(tline);
        Neighbour(docu,:) = tline;
        for i = 1:topK
           tline = fgetl(fVa);
           tline = str2num(tline);
           Matrix = [Matrix;tline];
        end;
        Matrix = Matrix + eye(topK,topK)*tol*trace(Matrix);
        ans = Matrix\ones(topK,1);
        ans = ans / sum(ans);
%        fprintf(fw,'%g\t',ans);
%        fprintf(fw,'\n');
        W(docu,:) = ans;
    end
    Neighbour = Neighbour+1;
    M = sparse(1:dnum,1:dnum,ones(1,dnum),dnum,dnum,4*topK*dnum); 
    for ii=1:dnum
       w = W(ii,:);
       jj = Neighbour(ii,:);
       M(ii,jj) = M(ii,jj) - w;
       M(jj,ii) = M(jj,ii) - w';
       M(jj,jj) = M(jj,jj) + w'*w;
    end;
    % CALCULATION OF EMBEDDING
    %options.disp = 0; 
    options.isreal = 1; options.issym = 1; 
    [Y,eigenvals] = eigs(M,d+2,0,options);
    %[Y,eigenvals] = jdqr(M,d+1);%change in using JQDR func
    Y = Y(:,2:d+1)'*sqrt(dnum); % bottom evect is [1,1,1,1...] with eval 0    
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
    for i=1:dnum
        %fprintf(fw,'%d ',Label(i));
        for j=1:d
            fprintf(fw,'%f\t',Y(j,i));
        end
        fprintf(fw,'\n');
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
    
end

fclose(fID);
fclose(fVa);
fclose(fw);






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
    fnum = 0;
    nnum = 0;
    anum = 0;
    for i=1:dnum
        if (Label(i)==1) FavorL = [FavorL,i]; fnum = fnum+1;end
        if (Label(i)==0) NoneL = [NoneL,i]; nnum = nnum+1;end
        if (Label(i)==-1) AgainstL = [AgainstL,i]; anum = anum+1;end
    end
    out_file_name = strcat('ans_SVM_train_',strcat(num2str(topic),'.txt'));
    fw=fopen(out_file_name,'w');
    %fprintf(fw,'%d\t%d\n',dnum,modelNum);
    for i=1:dnum
        %if (Label(i)~=0||i>modelNum)
        fprintf(fw,'%d\t',Label(i));
        for j=1:d
            fprintf(fw,'%d:%f\t',j,Y(j,i));
        end
        fprintf(fw,'\n');
        %end
        if (i==modelNum)
%             while (anum~=fnum||nnum~=fnum)
%                for k=1:modelNum
%                    if (Label(k)==1&&(fnum<nnum||fnum<anum))            
%                        fnum = fnum+1;
%                        fprintf(fw,'%d\t',Label(k));
%                        for j=1:d
%                            fprintf(fw,'%d:%f\t',j,Y(j,k));
%                        end
%                        fprintf(fw,'\n');
%                    end
%                    if (Label(k)==0&&(nnum<fnum||nnum<anum))            
%                        nnum = nnum+1;
%                        %fprintf(fw,'%d\t',Label(k));
%                        %for j=1:d
%                        %    fprintf(fw,'%d:%f\t',j,Y(j,k));
%                        %end
%                        %fprintf(fw,'\n');
%                    end
%                    if (Label(k)==-1&&(anum<nnum||anum<fnum))            
%                        anum = anum+1;
%                        fprintf(fw,'%d\t',Label(k));
%                        for j=1:d
%                            fprintf(fw,'%d:%f\t',j,Y(j,k));
%                        end
%                        fprintf(fw,'\n');
%                    end                   
%                end
%             end
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