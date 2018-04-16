segments = 6;
size = 1;

theta = 360 / segments;
phi = 90 - 180 / segments;

r = 2;
pi = 3.141592653589793;


points = [ 
    for (i = [0:2*segments])
    i % 2 == 0 ?
        [r*cos(i*theta/2) * sin(phi), r*sin(i*theta/2) * sin(phi)] :
        [r*cos(i*theta/2), r*sin(i*theta/2)]
];
polygon(points);