package com.hit.linkgame.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hit.linkgame.R;
import com.hit.linkgame.SoundPlay;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends BoardView {

	private static final int REFRESH_VIEW = 1;

	public static final int WIN = 1;
	public static final int LOSE = 2;
	public static final int PAUSE = 3;
	public static final int PLAY = 4;
	public static final int QUIT = 5;

	private int Help = 3;
	private int Refresh = 3;
	/**
	 * 第一关为100秒钟的时间
	 */
	private int totalTime = 100;
	private int leftTime;

	public static SoundPlay soundPlay;
	public MediaPlayer player;

	private RefreshTime refreshTime;
	private RefreshHandler refreshHandler = new RefreshHandler();
	/**
	 * 用来停止计时器的线程
	 */
	private boolean isStop;

	private OnTimerListener timerListener = null;
	private OnStateListener stateListener = null;
	private OnToolsChangeListener toolsChangedListener = null;

	private List<Point> path = new ArrayList<Point>();

	public GameView(Context context, AttributeSet atts) {
		super(context, atts);
		player = MediaPlayer.create(context, R.raw.back2new);
		player.setLooping(true);//设置循环播放
	}

	public static final int ID_SOUND_CHOOSE = 0;
	public static final int ID_SOUND_DISAPEAR = 1;
	public static final int ID_SOUND_WIN = 4;
	public static final int ID_SOUND_LOSE = 5;
	public static final int ID_SOUND_REFRESH = 6;
	public static final int ID_SOUND_TIP = 7;
	public static final int ID_SOUND_ERROR = 8;

	public void startPlay(){
		Help = 1000;
		Refresh = 3;
		isStop = false;
		toolsChangedListener.onRefreshChanged(Refresh);
		toolsChangedListener.onTipChanged(Help);
		leftTime = totalTime;
		initMap();
		player.start();
		refreshTime = new RefreshTime();
		refreshTime.start();//启动线程
		GameView.this.invalidate();

	}

	public void startNextPlay(){
		//下一关为上一关减去10秒的时间
        Level++;
        switch (Level)
        {
            case 2 :
                iconCounts = 12;
                break;
            case 3:
                iconCounts = 14;

                break;
            case 4:
                iconCounts = 16;

                break;
            case 5:
                iconCounts = 18;
                break;
                default:
                    break;
        }
		startPlay();
	}



	//游戏中的音效
	public static void initSound(Context context){
		soundPlay = new SoundPlay();
		soundPlay.initSounds(context);
		soundPlay.loadSfx(context, R.raw.choose, ID_SOUND_CHOOSE);
		soundPlay.loadSfx(context, R.raw.disappear1, ID_SOUND_DISAPEAR);
		soundPlay.loadSfx(context, R.raw.win, ID_SOUND_WIN);
		soundPlay.loadSfx(context, R.raw.lose, ID_SOUND_LOSE);
		soundPlay.loadSfx(context, R.raw.item1, ID_SOUND_REFRESH);
		soundPlay.loadSfx(context, R.raw.item2, ID_SOUND_TIP);
		soundPlay.loadSfx(context, R.raw.alarm, ID_SOUND_ERROR);
	}

	public void setOnTimerListener(OnTimerListener timerListener){
		this.timerListener = timerListener;
	}

	public void setOnStateListener(OnStateListener stateListener){
		this.stateListener = stateListener;
	}

	public void setOnToolsChangedListener(OnToolsChangeListener toolsChangedListener){
		this.toolsChangedListener = toolsChangedListener;
	}

	public void stopTimer()
    {
		isStop = true;
	}

	/*
	RefreshHandler
	根据传送的信息来进行考察时否需要刷新或者已经胜利了
	 */
	class RefreshHandler extends Handler
    {
		@Override
		public void handleMessage(Message msg)
        {
			super.handleMessage(msg);
			if (msg.what == REFRESH_VIEW)
			{
				GameView.this.invalidate();
				if (win())
				{
					setMode(WIN);
					soundPlay.play(ID_SOUND_WIN, 0);
					isStop = true;
				}
				else if (die())
				{
					change();
				}
			}
		}

		public void sleep(int delayTime) {
			this.removeMessages(0); //移除队列中下标为0的那条
			Message message = new Message();
			message.what = REFRESH_VIEW;
			sendMessageDelayed(message, delayTime);
		}
	}

	class RefreshTime extends Thread {

		public void run()
        {
			while (leftTime >= 0 && !isStop)//Isstop 成功消除所有的时候即为stop
            {
				timerListener.onTimer(leftTime);//用于刷新显示的剩余的时间
				leftTime--;
				try
                {
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
                {
					e.printStackTrace();
				}
			}
			if(!isStop){
				setMode(LOSE);//如果时间到了但是还未消除所有 则会
				soundPlay.play(ID_SOUND_LOSE, 0);
			}

		}
	}

	public int getTotalTime(){
		return totalTime;
	}

	public int getTipNum(){
		return Help;
	}

	public int getRefreshNum(){
		return Refresh;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();//获得相对坐标，xy是相对值 相对于师徒的左上点的坐标
		int y = (int) event.getY();
		Point p = screenToindex(x, y);
		if (map[p.x][p.y] > 0)
		{
			if (selected.size() == 1)
			{
				if (link(selected.get(0), p))
				{
					selected.add(p);
					drawLine(path.toArray(new Point[] {}));
					soundPlay.play(ID_SOUND_DISAPEAR, 0);
					refreshHandler.sleep(100);
				} else
					{
					selected.clear();
					selected.add(p);
					soundPlay.play(ID_SOUND_CHOOSE, 0);
					GameView.this.invalidate();
				}
			} else {
				selected.add(p);
				soundPlay.play(ID_SOUND_CHOOSE, 0);
				GameView.this.invalidate();
			}
		}
		return super.onTouchEvent(event);
	}

	public void initMap() {
	    if(Level == 1)
        {
            int x = 1;
            int y = 0;
            for (int i = 1; i < xCount - 1; i++)
            {
                for (int j = 1; j < yCount - 1; j++)
                {
                    map[i][j] = x;
                    if (y == 1) {
                        x++;
                        y = 0;
                        if (x == iconCounts + 1 ) { //修改此处可以更改游戏中出现的种类
                            x = 1;
                        }
                    } else {
                        y = 1;
                    }
                }
            }
        }
        else if(Level == 2)
        {
            int x = 11;
            int y = 0;
            for (int i = 1; i < xCount - 1; i++)
            {
                for (int j = 1; j < yCount - 1; j++)
                {
                    map[i][j] = x;
                    if (y == 1) {
                        x++;
                        y = 0;
                        if (x == iconCounts + 11) { //修改此处可以更改游戏中出现的水果种类
                            x = 11;
                        }
                    } else {
                        y = 1;
                    }
                }
            }
        }
        else if(Level == 3)
        {
            int x = 23;
            int y = 0;
            for (int i = 1; i < xCount - 1; i++)
            {
                for (int j = 1; j < yCount - 1; j++)
                {
                    map[i][j] = x;
                    if (y == 1) {
                        x++;
                        y = 0;
                        if (x == iconCounts +23 ) { //修改此处可以更改游戏中出现的水果种类
                            x = 23;
                        }
                    } else {
                        y = 1;
                    }
                }
            }
        }
        else if (Level == 4)
        {
            int x = 37;
            int y = 0;
            for (int i = 1; i < xCount - 1; i++)
            {
                for (int j = 1; j < yCount - 1; j++)
                {
                    map[i][j] = x;
                    if (y == 1) {
                        x++;
                        y = 0;
                        if (x == iconCounts + 37 ) { //修改此处可以更改游戏中出现的水果种类
                            x = 37;
                        }
                    } else {
                        y = 1;
                    }
                }
            }
        }
        else if (Level == 5)
        {
            int x = 53;
            int y = 0;
            for (int i = 1; i < xCount - 1; i++)
            {
                for (int j = 1; j < yCount - 1; j++)
                {
                    map[i][j] = x;
                    if (y == 1) {
                        x++;
                        y = 0;
                        if (x == iconCounts + 53 -1 ) { //修改此处可以更改游戏中出现的水果种类
                            x = 53;
                        }
                    } else {
                        y = 1;
                    }
                }
            }
        }

		change();
	}

//	public void TurnLeft()
//	{
//		for(int x = 1 ; x<xCount-1 ; x++)
//		{
//			for(int y = 1 ; y <yCount-1 ; x++)
//			{
//				if(map[x][y]==0)
//				{
//					for(int z = y+1 ; z <yCount -2 ; z++)
//					{
//						if(z == y+1)
//						{
//							map[x][y] = map[x][z];
//							map[x][z] = map[x][z+1];
//						}
//						else
//						{
//							map[x][z]=map[x][z+1];
//						}
//					}
//				}
//			}
//		}
//	}

	public void TurnLeftWhenChange()
	{
		for(int x=xCount-2 ; x>0 ; x-- )
		{
			for(int y = 1 ; y<yCount-1;y++)
			{
				if(map[x][y]==0)
				{
					for(int z = x ; z<xCount-1;z++)
					{
						if(z!=xCount-2)
						{
							map[z][y] = map[z+1][y];
						}
						else
						{
							map[z][y]=0;
						}
					}
				}
			}
		}
	}

	private void TurnRightWhenChange()
	{
		for(int x=1 ; x<xCount-1 ; x++ )
		{
			for(int y = 1 ; y<yCount-1;y++)
			{
				if(map[x][y]==0)
				{
					for(int z = x ; z > 0;z--)
					{
						if(z!=1)
						{
							map[z][y] = map[z-1][y];
						}
						else
						{
							map[z][y]=0;
						}
					}
				}
			}
		}
	}

	private void TurnUpWhenChange()
	{
		for(int y = yCount-2 ; y>0 ; y-- )
		{
			for(int x = 1 ; x<xCount-1 ; x++)
			{
				if(map[x][y]==0)
				{
					for(int z = y ; z <yCount-1 ; z++)
					{
						if(z != yCount-2)
						{
							map[x][z]=map[x][z+1];
						}
						else
						{
							map[x][z]=0;
						}
					}
				}
			}
		}
	}

	private void TurnDownWhenChange()
	{
		for(int y = 1 ; y<yCount-1 ; y++ )
		{
			for(int x = 1 ; x<xCount-1 ; x++)
			{
				if(map[x][y]==0)
				{
					for(int z = y ; z >0 ; z--)
					{
						if(z != 1)
						{
							map[x][z]=map[x][z+1];
						}
						else
						{
							map[x][z]=0;
						}
					}
				}
			}
		}
	}

	private void change() {
		Random random = new Random();
		int tmpV, tmpX, tmpY;
		for (int x = 1; x < xCount - 1; x++) {
			for (int y = 1; y < yCount - 1; y++) {
				tmpX = 1 + random.nextInt(xCount - 2);
				tmpY = 1 + random.nextInt(yCount - 2);
				tmpV = map[x][y];
				map[x][y] = map[tmpX][tmpY];
				map[tmpX][tmpY] = tmpV;
			}
		}
		if(Level==1)
		{
			TurnLeftWhenChange();
		}
		else if(Level ==2 )
		{
			TurnRightWhenChange();
		}
		else if (Level == 3)
		{
			TurnDownWhenChange();
		}
		else if (Level == 4)
		{
			TurnUpWhenChange();
		}
		else
		{
			if(randoms == 1)
			{
				TurnLeftWhenChange();
			}
			else if(randoms == 2)
			{
				TurnRightWhenChange();
			}
			else if (randoms == 3)
			{
				TurnDownWhenChange();
			}
			else if (randoms==4)
			{
				TurnUpWhenChange();
			}
			else
			{
				;
			}
		}
		if (die()) {
			change();
		}
		GameView.this.invalidate();
	}



	public void setMode(int stateMode) {
		this.stateListener.OnStateChanged(stateMode);
	}

	private boolean die() {
		for (int y = 1; y < yCount - 1; y++) {
			for (int x = 1; x < xCount - 1; x++) {
				if (map[x][y] != 0) {
					for (int j = y; j < yCount - 1; j++) {
						if (j == y) {
							for (int i = x + 1; i < xCount - 1; i++) {
								if (map[i][j] == map[x][y]
										&& link(new Point(x, y),
										new Point(i, j))) {
									return false;
								}
							}
						} else {
							for (int i = 1; i < xCount - 1; i++) {
								if (map[i][j] == map[x][y]
										&& link(new Point(x, y),
										new Point(i, j))) {
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	List<Point> p1E = new ArrayList<Point>();
	List<Point> p2E = new ArrayList<Point>();

	private boolean link(Point p1, Point p2) {
		if (p1.equals(p2)) {//如果点击的是同一个则会直接返回不连通
			return false;
		}
		path.clear();//清除路径
		if (map[p1.x][p1.y] == map[p2.x][p2.y]) {//当选中的两个图片类型相同的时候 做后续的判读
			if (linkD(p1, p2)) { //如果两个点在同一x轴或者y轴上能够用直线联通则直接调用linkD
				path.add(p1);
				path.add(p2);
				return true;
			}

			Point p = new Point(p1.x, p2.y);//转折一次 如果能够连接正确
			if (map[p.x][p.y] == 0) {
				if (linkD(p1, p) && linkD(p, p2)) {
					path.add(p1);
					path.add(p);
					path.add(p2);
					return true;
				}
			}
			p = new Point(p2.x, p1.y);
			if (map[p.x][p.y] == 0) {
				if (linkD(p1, p) && linkD(p, p2)) {
					path.add(p1);
					path.add(p);
					path.add(p2);
					return true;
				}
			}
			expandX(p1, p1E);
			expandX(p2, p2E);
			//两个折点的情况
			for (Point pt1 : p1E) {
				for (Point pt2 : p2E) {
					if (pt1.x == pt2.x) {
						if (linkD(pt1, pt2)) {
							path.add(p1);
							path.add(pt1);
							path.add(pt2);
							path.add(p2);
							return true;
						}
					}
				}
			}

			expandY(p1, p1E);
			expandY(p2, p2E);
			for (Point pt1 : p1E) {
				for (Point pt2 : p2E) {
					if (pt1.y == pt2.y) {
						if (linkD(pt1, pt2)) {
							path.add(p1);
							path.add(pt1);
							path.add(pt2);
							path.add(p2);
							return true;
						}
					}
				}
			}
			return false;
		}
		return false;
	}

	private boolean linkD(Point p1, Point p2) {//两个点在同一x轴或者Y轴的时候
		if (p1.x == p2.x) {
			int y1 = Math.min(p1.y, p2.y);
			int y2 = Math.max(p1.y, p2.y);
			boolean flag = true;
			for (int y = y1 + 1; y < y2; y++) {
				if (map[p1.x][y] != 0) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return true;
			}
		}
		if (p1.y == p2.y) {
			int x1 = Math.min(p1.x, p2.x);
			int x2 = Math.max(p1.x, p2.x);
			boolean flag = true;
			for (int x = x1 + 1; x < x2; x++) {
				if (map[x][p1.y] != 0) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return true;
			}
		}
		return false;
	}

	private void expandX(Point p, List<Point> l) { //在X轴上交进行扩展 寻找从该点开始的 连续的空白点
		l.clear();
		for (int x = p.x + 1; x < xCount; x++) {
			if (map[x][p.y] != 0) {
				break;
			}
			l.add(new Point(x, p.y));
		}
		for (int x = p.x - 1; x >= 0; x--) {
			if (map[x][p.y] != 0) {
				break;
			}
			l.add(new Point(x, p.y));
		}
	}

	private void expandY(Point p, List<Point> l) { //在Y轴上进行扩展，作用同上
		l.clear();
		for (int y = p.y + 1; y < yCount; y++) {
			if (map[p.x][y] != 0) {
				break;
			}
			l.add(new Point(p.x, y));
		}
		for (int y = p.y - 1; y >= 0; y--) {
			if (map[p.x][y] != 0) {
				break;
			}
			l.add(new Point(p.x, y));
		}
	}

	private boolean win() {
		for (int x = 0; x < xCount; x++) {
			for (int y = 0; y < yCount; y++) {
				if (map[x][y] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	public void autoClear() {
		if (Help == 0) {
			soundPlay.play(ID_SOUND_ERROR, 0);
		}else{
			soundPlay.play(ID_SOUND_TIP, 0);
			Help--;
			toolsChangedListener.onTipChanged(Help);
			drawLine(path.toArray(new Point[] {}));
			refreshHandler.sleep(100);
		}
	}

	public void refreshChange(){
		if(Refresh == 0){
			soundPlay.play(ID_SOUND_ERROR, 0);
			return;
		}else{
			soundPlay.play(ID_SOUND_REFRESH, 0);
			Refresh--;
			toolsChangedListener.onRefreshChanged(Refresh);
			change();
		}
	}
}
