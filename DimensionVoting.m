tol = 1e-5;
v = 0.95;

fMa=fopen('Matrix_Value.txt','r');
fid=fopen('Matrix_ID.txt','r');
for topic = 1:5
    tline = fgetl(fid);
    tline = fgetl(fMa);
    tline = str2num(tline);
    dnum = tline(1)
    topK = tline(2)  
    dall =zeros(1,dnum);
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
        ans = Matrix\ones(topK,1);
        ans = ans / sum(ans);
%        fprintf(fw,'%g\t',ans);
%        fprintf(fw,'\n');
        W(docu,:) = ans;
    end   
    tline = fgetl(fMa);
    tline = str2num(tline);
    dall
    
end

fclose(fid);
fclose(fMa);