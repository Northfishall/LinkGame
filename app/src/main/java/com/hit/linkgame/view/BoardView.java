package com.hit.linkgame.view;

import java.util.ArrayList;
import java.util.List;

import com.hit.linkgame.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class BoardView extends View {

	public int Level = 1;

	/**
	 * xCount x轴方向的图标数+1
	 */
	protected  static final int xCount =10;
	/**
	 * yCount y轴方向的图标数+1
	 */
	protected static final int  yCount =12;
	/**
	 * map 连连看游戏棋盘
	 */
	protected int[][] map = new int[xCount][yCount];
	/**
	 * iconSize 图标大小
	 */
	protected int iconSize;
	/**
	 * iconCounts 图标的数目
	 */
	protected int iconCounts=10;
	/**
	 * icons 所有的图片
	 */
	public Bitmap[] icons = new Bitmap[71];

	/**
	 * path 可以连通点的路径
	 */
	private Point[] path = null;
	/**
	 * selected 选中的图标
	 */
	protected List<Point> selected = new ArrayList<Point>();

	public BoardView(Context context,AttributeSet atts) {
		super(context,atts);

		calIconSize();

		Resources r = getResources();
//		loadBitmaps(1, r.getDrawable(R.drawable.fruit_01));
//		loadBitmaps(2, r.getDrawable(R.drawable.fruit_02));
//		loadBitmaps(3, r.getDrawable(R.drawable.fruit_03));
//		loadBitmaps(4, r.getDrawable(R.drawable.fruit_04));
//		loadBitmaps(5, r.getDrawable(R.drawable.fruit_05));
//		loadBitmaps(6, r.getDrawable(R.drawable.fruit_06));
//		loadBitmaps(7, r.getDrawable(R.drawable.fruit_07));
//		loadBitmaps(8, r.getDrawable(R.drawable.fruit_08));
//		loadBitmaps(9, r.getDrawable(R.drawable.fruit_09));
//		loadBitmaps(10, r.getDrawable(R.drawable.fruit_10));
//		loadBitmaps(11, r.getDrawable(R.drawable.fruit_11));
//		loadBitmaps(12, r.getDrawable(R.drawable.fruit_12));
//		loadBitmaps(13, r.getDrawable(R.drawable.fruit_13));
//		loadBitmaps(14, r.getDrawable(R.drawable.fruit_14));
//		loadBitmaps(15, r.getDrawable(R.drawable.fruit_15));
//		loadBitmaps(16, r.getDrawable(R.drawable.fruit_17));
//		loadBitmaps(17, r.getDrawable(R.drawable.fruit_18));
//		loadBitmaps(18, r.getDrawable(R.drawable.fruit_19));
		loadBitmaps(1,r.getDrawable(R.drawable.a1));
        loadBitmaps(2,r.getDrawable(R.drawable.a2));
        loadBitmaps(3,r.getDrawable(R.drawable.a3));
        loadBitmaps(4,r.getDrawable(R.drawable.a4));
        loadBitmaps(5,r.getDrawable(R.drawable.a5));
        loadBitmaps(6,r.getDrawable(R.drawable.a6));
        loadBitmaps(7,r.getDrawable(R.drawable.a7));
        loadBitmaps(8,r.getDrawable(R.drawable.a8));
        loadBitmaps(9,r.getDrawable(R.drawable.a9));
        loadBitmaps(10,r.getDrawable(R.drawable.a10));
        loadBitmaps(11,r.getDrawable(R.drawable.photo_01));
        loadBitmaps(12,r.getDrawable(R.drawable.photo_02));
        loadBitmaps(13,r.getDrawable(R.drawable.photo_03));
        loadBitmaps(14,r.getDrawable(R.drawable.photo_04));
        loadBitmaps(15,r.getDrawable(R.drawable.photo_05));
        loadBitmaps(16,r.getDrawable(R.drawable.photo_06));
        loadBitmaps(17,r.getDrawable(R.drawable.photo_07));
        loadBitmaps(18,r.getDrawable(R.drawable.photo_08));
        loadBitmaps(19,r.getDrawable(R.drawable.photo_09));
        loadBitmaps(20,r.getDrawable(R.drawable.photo_10));
        loadBitmaps(21,r.getDrawable(R.drawable.photo_11));
        loadBitmaps(22,r.getDrawable(R.drawable.photo_12));
        loadBitmaps(23,r.getDrawable(R.drawable.photo_13));
        loadBitmaps(24,r.getDrawable(R.drawable.skill1));
        loadBitmaps(25,r.getDrawable(R.drawable.skill2));
        loadBitmaps(26,r.getDrawable(R.drawable.skill3));
        loadBitmaps(27,r.getDrawable(R.drawable.skill4));
        loadBitmaps(28,r.getDrawable(R.drawable.skill5));
        loadBitmaps(29,r.getDrawable(R.drawable.skill6));
        loadBitmaps(30,r.getDrawable(R.drawable.skill7));
        loadBitmaps(31,r.getDrawable(R.drawable.skill8));
        loadBitmaps(32,r.getDrawable(R.drawable.skill9));
        loadBitmaps(33,r.getDrawable(R.drawable.skill10));
        loadBitmaps(34,r.getDrawable(R.drawable.skill11));
        loadBitmaps(35,r.getDrawable(R.drawable.skill12));
        loadBitmaps(36,r.getDrawable(R.drawable.skill13));
        loadBitmaps(37,r.getDrawable(R.drawable.skill14));
        loadBitmaps(38,r.getDrawable(R.drawable.skill15));
	}

	/**
	 *
	 * 计算图标的长宽
	 */
	private void calIconSize()
	{
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager()
				.getDefaultDisplay().getMetrics(dm);
		iconSize = dm.widthPixels/(xCount);
	}

	/**
	 *
	 * @param key 特定图标的标识
	 * @param d drawable下的资源
	 */
	public void loadBitmaps(int key,Drawable d){
		Bitmap bitmap = Bitmap.createBitmap(iconSize,iconSize,Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		d.setBounds(0, 0, iconSize, iconSize);
		d.draw(canvas);

		icons[key]=bitmap;
	}

	public void TurnLeft(Point a , Point b)
	{
		int x1 ;
		int y1 ;
		int x2 ;
		int y2 ;
		if(a.x>b.x)
		{
			x1 = a.x;
			y1 = a.y;
			x2 = b.x;
			y2 = b.y;
		}
		else
		{
			x1 = b.x;
			y1 = b.y;
			x2 = a.x;
			y2 = a.y;
		}
		for(int i = x1 ; i<xCount-1 ; i++)
		{
			if(i != xCount-2)
			{
				map[i][y1] = map[i+1][y1];
			}
			else
			{
				map[i][y1]=0;
			}
		}

		for(int i = x2 ; i<xCount-1 ; i++)
		{
			if(i != xCount-2)
			{
				map[i][y2] = map[i+1][y2];
			}
			else
			{
				map[i][y2]=0;
			}
		}
	}

	public void TurnRight(Point a , Point b)
	{

        int x1 ;
        int y1 ;
        int x2 ;
        int y2 ;
        if(a.x<b.x)
        {
            x1 = a.x;
            y1 = a.y;
            x2 = b.x;
            y2 = b.y;
        }
        else
        {
            x1 = b.x;
            y1 = b.y;
            x2 = a.x;
            y2 = a.y;
        }
        for(int i = x1 ; i>0 ; i--)
        {
            if(i != 1)
            {
                map[i][y1] = map[i-1][y1];
            }
            else
            {
                map[i][y1]=0;
            }
        }

        for(int i = x2 ; i>0 ; i--)
        {
            if(i != 1)
            {
                map[i][y2] = map[i-1][y2];
            }
            else
            {
                map[i][y2]=0;
            }
        }
	}

	public void TurnDown(Point a ,Point b)
	{
		int x1 ;
		int y1 ;
		int x2 ;
		int y2 ;
		if(a.y<b.y)
		{
			x1 = a.x;
			y1 = a.y;
			x2 = b.x;
			y2 = b.y;
		}
		else
		{
			x1 = b.x;
			y1 = b.y;
			x2 = a.x;
			y2 = a.y;
		}
		for(int i = y1 ; i > 0 ; i--)
		{
			if(i != 1)
			{
				map[x1][i] = map[x1][i-1];
			}
			else
			{
				map[x1][i]=0;
			}
		}

		for(int i = y2 ; i > 0 ; i--)
		{
			if(i != 1)
			{
				map[x2][i] = map[x2][i-1];
			}
			else
			{
				map[x2][i]=0;
			}
		}
	}

	public void TurnUp(Point a ,Point b)
	{
		int x1 ;
		int y1 ;
		int x2 ;
		int y2 ;
		if(a.y>b.y)
		{
			x1 = a.x;
			y1 = a.y;
			x2 = b.x;
			y2 = b.y;
		}
		else
		{
			x1 = b.x;
			y1 = b.y;
			x2 = a.x;
			y2 = a.y;
		}
		for(int i = y1 ; i < yCount-1 ; i++)
		{
			if(i != yCount-2)
			{
				map[x1][i] = map[x1][i+1];
			}
			else
			{
				map[x1][i]=0;
			}
		}

		for(int i = y2 ; i < yCount-1 ; i++)
		{
			if(i != yCount-2)
			{
				map[x2][i] = map[x2][i+1];
			}
			else
			{
				map[x2][i]=0;
			}
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {

		/**
		 * 绘制连通路径，然后将路径以及两个图标清除
		 */
		if (path != null && path.length >= 2) {
			for (int i = 0; i < path.length - 1; i++) {
				Paint paint = new Paint();
				paint.setColor(Color.CYAN);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(3);
				Point p1 = indextoScreen(path[i].x, path[i].y);
				Point p2 = indextoScreen(path[i + 1].x, path[i + 1].y);
				canvas.drawLine(p1.x + iconSize / 2, p1.y + iconSize / 2,
						p2.x + iconSize / 2, p2.y + iconSize / 2, paint);
			}
			Point temp0 = path[0];
			Point temp1  = path[path.length-1];
			Point p = path[0];
			map[p.x][p.y] = 0;
			p = path[path.length - 1];
			map[p.x][p.y] = 0;
			if(Level == 1)
			{
				TurnLeft(temp0,temp1);
                //TurnRight(temp0,temp1);
				//TurnUp(temp0,temp1);
			}
			else if (Level ==2)
			{
				TurnRight(temp0,temp1);
			}
			selected.clear();
			path = null;
		}
		/**
		 * 绘制棋盘的所有图标 当这个坐标内的值大于0时绘制
		 */

		for(int x=0;x<map.length;x+=1){
			for(int y=0;y<map[x].length;y+=1){
				if(map[x][y]>0){
					Point p = indextoScreen(x, y);
					canvas.drawBitmap(icons[map[x][y]], p.x,p.y,null);
				}
			}
		}

		/**
		 * 绘制选中图标，当选中时图标放大显示
		 */
		for(Point position:selected){
			Point p = indextoScreen(position.x, position.y);
			if(map[position.x][position.y] >= 1){
				canvas.drawBitmap(icons[map[position.x][position.y]],
						null,
						new Rect(p.x-5, p.y-5, p.x + iconSize + 5, p.y + iconSize + 5), null);
			}
		}
	}

	/**
	 *
	 * @param path
	 */
	public void drawLine(Point[] path) {
		this.path = path;
		this.invalidate();
	}

	/**
	 * 工具方法
	 * @param x 数组中的横坐标
	 * @param y 数组中的纵坐标
	 * @return 将图标在数组中的坐标转成在屏幕上的真实坐标
	 */
	public Point indextoScreen(int x,int y){
		return new Point(x* iconSize , y * iconSize );
	}
	/**
	 * 工具方法
	 * @param x 屏幕中的横坐标
	 * @param y 屏幕中的纵坐标
	 * @return 将图标在屏幕中的坐标转成在数组上的虚拟坐标
	 */
	public Point screenToindex(int x,int y){
		int ix = x/ iconSize;
		int iy = y / iconSize;
		if(ix < xCount && iy <yCount){
			return new Point( ix,iy);
		}else{
			return new Point(0,0);
		}
	}
}
