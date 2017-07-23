package com.truthower.suhang.mangareader.widget.shotview;

import android.graphics.Point;

public class Calc
{
	public static int getPointDis(Point point1, Point point2) {
		int dis = 0;
		int abs_x = point1.x - point2.x;
		int abs_y = point1.y - point2.y;
		dis = ( int ) Math.sqrt( Math.pow( abs_x, 2 ) + Math.pow( abs_y, 2 ) );
		return dis;
	}
	
}
