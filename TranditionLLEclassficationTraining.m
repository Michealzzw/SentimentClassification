tol = 1e-8;
d = 10;

for LID = -1:1
    if (LID == -1) sentiment = 'AGAINST'; color = 'r';end
    if (LID == 0) sentiment = 'NONE'; color = 'k';end
    if (LID == 1) sentiment = 'FAVOR'; color = 'g';end
    fMa=fopen(strcat(strcat('Matrix_Matlab_',sentiment),'.txt'),'r');
    fid=fopen(strcat(strcat('Matrix_ID_',sentiment),'.txt'),'r');
    fw=fopen(strcat(strcat('ans_',sentiment),'.txt'),'w');
    for topic = 1:5
        tline = fgetl(fid);
        tline = fgetl(fMa);
        tline = str2num(tline);
        dnum = tline(1)
        topK = tline(2); 
        W = zeros(dnum,topK);
        Neighbour = zeros(dnum,topK);
        for docu = 1:dnum
            Matrix = [];
            tline = fgetl(fid);
            tline = str2num(tline);
            Neighbour(docu,:) = tline;
            for i = 1:topK
               tline = fgetl(fMa);
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
        for i=1:dnum
            for j=1:d
                fprintf(fw,'%f\t',Y(j,i));
            end
            fprintf(fw,'\n');
        end
        tline = fgetl(fMa);
        tline = str2num(tline);
        Label = tline;
        NowL = [];
        for i=1:dnum
            if (Label(i)==LID) NowL = [NowL,i];end
        end
        %figure(topic)
        %
        subplot(1,5,topic);
        hold on    
        scatter3(Y(1,NowL),Y(2,NowL),Y(3,NowL),color)

    end

    fclose(fid);
    fclose(fMa);
    fclose(fw);

end
