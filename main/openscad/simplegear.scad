function trapezoid(wid1, wid2, height) = [
    [-wid1/2, -height/2],
    [ wid1/2, -height/2],
    [ wid2/2,  height/2],
    [-wid2/2,  height/2],
    [-wid1/2, -height/2]
];

pi = 3.141592653589793;

sides=10;

tooth_base_width=10;
tooth_peak_width=7;
tooth_height=7;
gear_thickness=10;

module tooth(base_width, peak_width, height, thickness) {
    union() {
        linear_extrude(height=thickness, center=true)
            polygon(
                trapezoid(base_width, peak_width, height)
            );
            translate([0, -height/2-thickness/4, 0])
                cube([base_width, thickness/2, thickness], center=true);
    };
}

module gear(tooth_base_width, tooth_peak_width, tooth_height, thickness, tooth_count) {
    
    theta=360/tooth_count;
    phi=(180-tooth_count)/2;

    tooth_section_width = tooth_base_width + tooth_peak_width;

    r = tooth_section_width * sin(phi) / sin(theta) * 0.9435;

    theta1 = 2*asin(tooth_base_width/(2*r));
    theta2prime = 2*asin(tooth_peak_width/(2*r));
    theta2 = theta - theta1;
    delta = theta - (theta1+theta2prime);
    
    echo(r, theta2, theta2prime, delta);

   union() {
       for (i = [0:tooth_count]) {
           toothangle = i*theta + theta1/2;
           dx1 = (r+tooth_height/2)*cos(toothangle);
           dy1 = (r+tooth_height/2)*sin(toothangle);
           translate([dx1, dy1, 0])
               rotate([0, 0, toothangle-90])
                   tooth(tooth_base_width, tooth_peak_width, tooth_height, gear_thickness);
           flatangle = i*theta + theta1 + theta2/2;
           dx2 = (r-thickness*3/4/2)*cos(flatangle);
           dy2 = (r-thickness*3/4/2)*sin(flatangle);
           translate([dx2, dy2, 0])
               rotate([0, 0, flatangle-90])
                   cube([tooth_peak_width, thickness*3/4, thickness], center=true);
       }
       cylinder(h=6*gear_thickness/7, r1=r, r2=r, center=true, $fn=90);
   }    
}

gear(tooth_base_width, tooth_peak_width, tooth_height, gear_thickness, sides);

