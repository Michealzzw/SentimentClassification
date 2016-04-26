v = 0.90;
for LID = -1:1
    if (LID == -1) sentiment = 'AGAINST'; color = 'r';end
    if (LID == 0) sentiment = 'NONE'; color = 'k';end
    if (LID == 1) sentiment = 'FAVOR'; color = 'g';end
    fMa=fopen(strcat(strcat('Matrix_Matlab_',sentiment),'.txt'),'r');
    fid=fopen(strcat(strcat('Matrix_ID_',sentiment),'.txt'),'r');
    for topic = 1:5        
        tline = fgetl(fid);
        tline = fgetl(fMa);
        tline = str2num(tline);
        dnum = tline(1)
        topK = tline(2); 
        dall = zeros(1,dnum);
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
            options.isreal = 1; options.issym = 1; 
            [tempMatrix,eigenvals] = eigs(Matrix,topK-2,0,options);
            totaleigval = trace(eigenvals);
            eigsum = 0.0;
            for j = 1:dnum-1
                eigsum = eigsum+eigenvals(j,j);
                if (eigsum/totaleigval>v)
                    dnow = j-1;
                    break;
                end
            end
            dall(docu) = dnow;
    %        fprintf(fw,'%g\t',ans);
    %        fprintf(fw,'\n');
            W(docu,:) = ans;
        end
        dall
        tline = fgetl(fMa);
        tline = str2num(tline);
        Label = tline;
        NowL = [];
        for i=1:dnum
            if (Label(i)==LID) NowL = [NowL,i];end
        end
    end

    fclose(fid);
    fclose(fMa);

end
