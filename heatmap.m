clear;clc;
fMa=fopen('heatmap.csv','r');
A = zeros(11,11);
for i = 5:15
    tline = fgetl(fMa);
    tline = str2num(tline);
    A(i-4,:) = tline;
end
hm = imagesc(5:15,5:15,A,[0.65,0.70]);
colormap(gray);
figure(gcf);colorbar;

