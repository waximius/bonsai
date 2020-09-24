//////////////////////////////////////////////////////////
/* Bezier Surface, Copyright 2001-2010 Ryoichi Mizuno   */
/* ryoichi[at]mizuno.org                                */
/* Dept. of Complexity Science and Engineering          */
/* at The University of Tokyo                           */
//////////////////////////////////////////////////////////
package com.rootedconcepts.deprecated;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

public class Example extends Applet implements Runnable {

    Thread th;
    Image buf_i;
    Graphics buf_g;
    boolean goFlag = true, growFlag = true, lineFlag = true, plineFlag = true, fpolyFlag = true;

    int w, h, xp0, yp0, stack, step;

    int MAX = 100;
    double x[] = new double[MAX];
    double y[] = new double[MAX];
    double z[] = new double[MAX];
    int t[] = new int[MAX];
    int p[] = new int[MAX];

    String str, str_next = "";

    int phi = 25, alpha = 32, beta = 25, scale = 35, stepmax = 15, view = 0;

    //GUI
    Button startstop, reset, b_line, b_polyline, b_fillpoly;

    public void init() {
        this.setSize(800, 600);
        
        //get screen size
        w = getSize().width - 20;
        h = getSize().height - 100;
        //create buffer layer
        buf_i = createImage(w, h);
        buf_g = buf_i.getGraphics();
        init_values();
        //GUI
        setLayout(new FlowLayout(1, 20, h + 30));
        add(b_line = new Button("hide"));
        add(b_polyline = new Button("hide"));
        add(b_fillpoly = new Button("hide"));
        add(startstop = new Button("stop"));
        add(reset = new Button("reset"));
        b_line.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                linectrl();
            }
        });
        b_polyline.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                polylinectrl();
            }
        });
        b_fillpoly.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fillpolyctrl();
            }
        });
        startstop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startstop_p();
            }
        });
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset_p();
            }
        });
    }

    public void start() {
        th = new Thread(this);
        th.start();
    }

    public void stop() {
        goFlag = false;
    }

    public void run() {
        while (goFlag) {
            repaint();
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
            }
        }
    }

    public void update(Graphics g) {
        this.paint(g);
    }

    public void paint(Graphics g) {
        //initialize
        if (growFlag) {
            step++;
        } else {
            view += 5;
        }
        if (goFlag) {
            draw();
        }
        //System.out.println(str);
        g.drawImage(buf_i, 10, 10, this);
        if (step >= stepmax) {
            growFlag = false;
        }
        //start message
        g.drawString("frame", w / 2 - 125, h + 75);
        g.drawString("polygon", w / 2 - 70, h + 75);
        g.drawString("filled", w / 2 - 5, h + 70);
        g.drawString("polygon", w / 2 - 10, h + 80);
        g.drawString("start", w / 2 + 55, h + 70);
        g.drawString("/stop", w / 2 + 55, h + 80);
        g.drawString("initialize", w / 2 + 105, h + 75);
    }

    public void draw() {
        init_b();
        init_point();
        str_next = "";
        read();
        str = str_next;
    }

    public void init_point() {
        x[0] = 0;
        y[0] = 0;
        z[0] = 0;
        t[0] = 0;
        p[0] = 45;
        xp0 = w / 2;
        yp0 = 0;
    }

    public void init_values() {
        str = "A(-5)";
        stack = 0;
        step = 0;
    }

    public void init_b() {
        buf_g.setColor(Color.white);
        buf_g.fillRect(0, 0, w, h);
    }

    public void read() {
        int len, now = 0, param, endp;
        String str_buf;
        char type;
        //get length of string pn
        len = str.length();
        //decode string one by one
        while (now < len) {
            type = str.charAt(now);
            if (type == '[') {
                stack++;
                x[stack] = x[0];
                y[stack] = y[0];
                z[stack] = z[0];
                t[stack] = t[0];
                p[stack] = p[0];
                str_next += "[";
            } else if (type == ']') {
                x[0] = x[stack];
                y[0] = y[stack];
                z[0] = z[stack];
                t[0] = t[stack];
                p[0] = p[stack];
                stack--;
                str_next += "]";
            } else {
                now++;
                str_buf = str.substring(now + 1, len);
                endp = str_buf.indexOf(")");
                param = java.lang.Integer.parseInt(str_buf.substring(0, endp));
                now += (endp + 1);
                decode(type, param);
            }
            now++;
        }
    }

    public void decode(char type, int param) {
        if (type == 'F') {
            forward(param);
        } else if (type == 'A') {
            apex(param);
        } else if (type == 'B') {
            bud(param);
        } else if (type == '+') {
            t[0] = (t[0] + param);
            str_next += "+(" + param + ")";
        } else if (type == '-') {
            t[0] = (t[0] - param);
            str_next += "-(" + param + ")";
        } else if (type == '/') {
            p[0] = (p[0] + param);
            str_next += "/(" + param + ")";
        }
    }

    public void forward(int r) {
        double x1, y1, z1;
        int xp1, yp1;
        x1 = x[0] - (double) scale * Math.sin((double) t[0] * Math.PI / 180) * Math.cos((double) p[0] * Math.PI / 180);
        y1 = y[0] - (double) scale * Math.sin((double) t[0] * Math.PI / 180) * Math.sin((double) p[0] * Math.PI / 180);
        z1 = z[0] + (double) scale * Math.cos((double) t[0] * Math.PI / 180);
        //start draw line
        xp0 = (int) ((double) w / 2 + (y[0] * Math.cos(view * Math.PI / 180) + x[0] * Math.sin(view * Math.PI / 180)));
        yp0 = (int) ((double) h - z[0]);
        xp1 = (int) ((double) w / 2 + (y1 * Math.cos(view * Math.PI / 180) + x1 * Math.sin(view * Math.PI / 180)));
        yp1 = (int) ((double) h - z1);
        if (fpolyFlag || plineFlag) {
            int xpoly[] = new int[4], ypoly[] = new int[4];
            double psi;
            if (yp1 - yp0 == 0) {
                psi = Math.PI / 2;
            } else if (xp1 - xp0 == 0) {
                psi = 0;
            } else {
                psi = Math.atan((xp1 - xp0) / (yp1 - yp0)); //[rad.]
            }
            xpoly[0] = (int) ((double) xp0 + (double) r * Math.cos(psi));
            ypoly[0] = (int) ((double) yp0 - (double) r * Math.sin(psi));
            xpoly[1] = (int) ((double) xp0 - (double) r * Math.cos(psi));
            ypoly[1] = (int) ((double) yp0 + (double) r * Math.sin(psi));
            xpoly[2] = (int) ((double) xp1 - (double) (r - 1) * Math.cos(psi));
            ypoly[2] = (int) ((double) yp1 + (double) (r - 1) * Math.sin(psi));
            xpoly[3] = (int) ((double) xp1 + (double) (r - 1) * Math.cos(psi));
            ypoly[3] = (int) ((double) yp1 - (double) (r - 1) * Math.sin(psi));
            Polygon branch = new Polygon(xpoly, ypoly, 4);
            if (fpolyFlag) {
                buf_g.setColor(Color.black);
                buf_g.fillPolygon(branch);
            }
            if (plineFlag) {
                buf_g.setColor(Color.red);
                buf_g.drawPolygon(branch);
            }
        }
        if (lineFlag) {
            buf_g.setColor(Color.blue);
            buf_g.drawLine(xp0, yp0, xp1, yp1);
        }
        //end draw line
        x[0] = x1;
        y[0] = y1;
        z[0] = z1;
        if (growFlag) {
            str_next += "F(" + (r + 1) + ")";
        } else {
            str_next += "F(" + r + ")";
        }
    }

    public void apex(int k) {
        if (growFlag) {
            if (k <= 0) {
                str_next += "F(1)A(" + (k + 1) + ")";
            } else {
                double prob;
                prob = Math.min(1.0, (2 * (double) k + 1) / Math.pow((double) k, 2));
                /*if(0.5>Math.random()){
                 //p1
                 if(prob>Math.random()){
                 str_next+="[+("+(-alpha)+")F(1)A("+(k+1)+")]-("+(-beta)+")F(1)A("+(k+1)+")";
                 }
                 //p2
                 else{
                 str_next+="B(1)-("+(-beta)+")F(1)A("+(k+1)+")";
                 }
                 }
                 else{
                 //p1
                 if(prob>Math.random()){
                 str_next+="[+("+alpha+")F(1)A("+(k+1)+")]-("+beta+")F(1)A("+(k+1)+")";
                 }
                 //p2
                 else{
                 str_next+="B(1)-("+beta+")F(1)A("+(k+1)+")";
                 }
                 }*/
                if (0.5 > Math.random()) {
                    if (0.5 > Math.random()) {
                        //p1
                        if (prob > Math.random()) {
                            str_next += "/(" + phi + ")[+(" + (-alpha) + ")F(1)A(" + (k + 1) + ")]-(" + (-beta) + ")F(1)A(" + (k + 1) + ")";
                        } //p2
                        else {
                            str_next += "/(" + phi + ")B(1)-(" + (-beta) + ")F(1)A(" + (k + 1) + ")";
                        }
                    } else {
                        //p1
                        if (prob > Math.random()) {
                            str_next += "/(" + phi + ")[+(" + alpha + ")F(1)A(" + (k + 1) + ")]-(" + beta + ")F(1)A(" + (k + 1) + ")";
                        } //p2
                        else {
                            str_next += "/(" + phi + ")B(1)-(" + beta + ")F(1)A(" + (k + 1) + ")";
                        }
                    }
                } else {
                    if (0.5 > Math.random()) {
                        //p1
                        if (prob > Math.random()) {
                            str_next += "/(" + (-phi) + ")[+(" + (-alpha) + ")F(1)A(" + (k + 1) + ")]-(" + (-beta) + ")F(1)A(" + (k + 1) + ")";
                        } //p2
                        else {
                            str_next += "/(" + (-phi) + ")B(1)-(" + (-beta) + ")F(1)A(" + (k + 1) + ")";
                        }
                    } else {
                        //p1
                        if (prob > Math.random()) {
                            str_next += "/(" + (-phi) + ")[+(" + alpha + ")F(1)A(" + (k + 1) + ")]-(" + beta + ")F(1)A(" + (k + 1) + ")";
                        } //p2
                        else {
                            str_next += "/(" + (-phi) + ")B(1)-(" + beta + ")F(1)A(" + (k + 1) + ")";
                        }
                    }
                }
            }
        } else {
            str_next += "A(" + k + ")";
        }
    }

    public void bud(int s) {
        //start draw circle
        xp0 = (int) ((double) w / 2 + y[0]);
        yp0 = (int) ((double) h - z[0]);
        //buf_g.setColor(Color.green);
        //buf_g.fillOval(xp0,yp0,s,s);
        //end draw cirle
        if (growFlag) {
            str_next += "B(" + (s + 1) + ")";
        } else {
            str_next += "B(" + s + ")";
        }
    }

    public void startstop_p() {
        if (goFlag) {
            goFlag = false;
            startstop.setLabel("start");
        } else {
            goFlag = true;
            start();
            startstop.setLabel("stop");
        }
    }

    public void reset_p() {
        init_b();
        init_point();
        init_values();
        growFlag = true;
        goFlag = false;
        startstop.setLabel("start");
        startstop_p();
    }

    public void linectrl() {
        if (lineFlag) {
            lineFlag = false;
            b_line.setLabel("show");
        } else {
            lineFlag = true;
            b_line.setLabel("hide");
        }
    }

    public void polylinectrl() {
        if (plineFlag) {
            plineFlag = false;
            b_polyline.setLabel("show");
        } else {
            plineFlag = true;
            b_polyline.setLabel("hide");
        }
    }

    public void fillpolyctrl() {
        if (fpolyFlag) {
            fpolyFlag = false;
            b_fillpoly.setLabel("show");
        } else {
            fpolyFlag = true;
            b_fillpoly.setLabel("hide");
        }
    }
}
