tol = 1e-8;
d = 10;

fMa=fopen('st_Matrix_Matlab.txt','r');
fid=fopen('st_Matrix_ID.txt','r');
fw=fopen('ans.txt','w');
Y1 = load('ans_FAVOR.txt');
Y2 = load('ans_NONE.txt');
Y3 = load('ans_AGAINST.txt');
num1 = 1;
num2 = 1;
num3 = 1;
mark = 0;
for topic = 1:5
    tline = fgetl(fid);
    tline = fgetl(fMa);
    tline = str2num(tline);
    dnum = tline(1)
    topK = tline(2)
    Ymodel = Yall(1+mark:mark+dnum,:);
    mark = mark + dnum;
    Y = zeros(dnum,d);
    W = zeros(dnum,topK);
    Neighbour = zeros(dnum,topK);
    for docu = 1:dnum
        tline = fgetl(fid);
        tline = str2num(tline);
        for i = 1:topK
           tline = fgetl(fMa);
           tline = str2num(tline);
        end;
    end
    tline = fgetl(fMa);
    tline = str2num(tline);
    Label = tline;
    for i=1:dnum
        if (Label(i)==1)
            nowpf = Y1(num1,:) ;
           % num1 = num1+1; fprintf(fw,'1\t');
        end
        if (Label(i)==0) 
            nowpf = Y2(num2,:) ;
           % num2 = num2+1;fprintf(fw,'0\t');
        end
        if (Label(i)==-1) 
            nowpf = Y3(num3,:);
            %num3 = num3+1;fprintf(fw,'-1\t'); 
        end
        for j=1:d
            fprintf(fw,'%f\t',nowpf(j));
        end
        fprintf(fw,'\n');
    end
    
end

fclose(fid);
fclose(fMa);
fclose(fw);