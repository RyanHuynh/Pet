import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import ucigame.*;


public class Pet extends Ucigame
{
	Animal pet;
	int timer = 0; //time of game
	int seed = 200; //use to change time
	int counter = 100; //use to calculate hunger and health
	int defaultTextY = 58; //X position of text string
	int defaultTextX = 20; //Y position of text string
	int textDuration = 300;
	int foodChoiceRate;
	int ghostRotate; 	
	boolean spawn = true;
	int playCounter = 50;
	int numberOfCatch = 4;
	Sprite hungerBar, healthBar,boredomBar, feedButton, sleepButton, wakeButton, butterfly,
					playButton, foodChoices, food1, food2, food3, textBackground, currentFood,
					good1, good2, good3,bad1, bad2, bad3, ghost, introPlay, credit, introCredit;
	ArrayList<Sprite> healthyFoodList = new ArrayList<Sprite>();
	ArrayList<Sprite> unhealthyFoodList = new ArrayList<Sprite>();
	Hashtable<Sprite, Food> foodMap = new Hashtable<Sprite, Food>();
	private class Animal
	{ 
		private Sprite pet, sleep, foodTray, heart;		
		private int health;
		private int boredom;
		private int hunger;
		private int eatCounter = 350;
		private int sleepCounter = 2000;
		private String mode;
		private Animal()
		{
			health = 100;
			boredom = 100;
			hunger = 100;
			sleep = makeSprite(97,116);
			sleep.addFrames(getImage("../Resources/Image/sleep.png"),
											0, 0,
											91, 0,
											182, 0,
											279,0);
			sleep.framerate(4);
			sleep.hide();
			pet = makeSprite(66,66);
			pet.addFrames(getImage("../Resources/Image/penguin.png"),
										0, 0,
										66, 0,
										132, 0,
										198, 0,
										264, 0,
										330, 0,
										396, 0,
										462,0,										
										0, 66,
										66, 66,
										132, 66,
										198, 66,
										264, 66,
										330, 66,
										396, 66,
										462,66,
										528,0,
										528,66);
			pet.pin(sleep, 50, -100);
			foodTray = makeSprite(getImage("../Resources/Image/foodTray.png"));
			pet.pin(foodTray, -18, 45);
			foodTray.hide();
			heart = makeSprite(103,95);
			heart.addFrames(getImage("../Resources/Image/heart.png"),
									0,0,
									103,0,
									206,0,
									309,0);
			pet.pin(heart, 50, -100);
			heart.framerate(5);
			heart.hide();
			pet.defineSequence("left", 0, 1, 2, 3, 4, 5, 6, 7);
			pet.defineSequence("right", 8, 9, 10, 11, 12, 13, 14, 15);
			pet.defineSequence("standleft",5);
			pet.defineSequence("standright",10);
			pet.defineSequence("sleep", 16);
			pet.defineSequence("eat", 17,17,5,5,5,5);
			pet.motion(-2, 0);
			pet.play("left");			
			pet.position(canvas.width() / 2,canvas.height() - 66);			
			mode = "wandering";
			pet.framerate(8);
		}	
		public void wandering()
		{			
			int key = 4;
			if((new Random()).nextInt(seed) == 0)
			{
				key = (new Random()).nextInt(4);				
			}
			if(pet.x() <= 5)
				key = 1;
			else if(pet.x() >= canvas.width() - 80)
				key = 0;
			
			if(key == 0)//walk left
			{ 
				pet.motion(-2, 0);
				pet.play("left");
				seed = 200;
			}				
			else if(key == 1)//walk right
			{  
				pet.motion(2, 0);
				pet.play("right");
				seed = 200;
			}
			else if(key == 2)///stand face left
			{
				pet.motion(0, 0);
				pet.play("standleft");
				seed = 50;
			}
			else if(key == 3) //stand face right
			{   
				pet.play("standright");
				pet.motion(0, 0);
				seed = 50;
			}			
			pet.move();
		}
		public void sleep()
		{
			pet.play("sleep");
			sleep.show();
			if(sleepCounter % 20 == 0 && health != 100)
				health++;
			if(sleepCounter != 0)
				sleepCounter--;
		}
		public void eat()	
		{			
			pet.play("eat");			
			foodTray.show();
			heart.show();
			eatCounter--;
			feedButton.hide();
			playButton.hide();
			sleepButton.hide();
			if(eatCounter == 0)
			{
				foodTray.hide();
				showButton();
				heart.hide();
				seed = 50;
				eatCounter = 350;
				pet.play("standleft");
				mode = "wandering";	
			}
		}	
		public void play()
		{			
			if(spawn)
			{
				butterfly.show();	
				if(butterfly.x() > pet.x())
				{
					pet.play("right");
					pet.motion(5, 0);
					butterfly.motion(1.5,0);
				}
				else
				{
					pet.play("left");
					pet.motion(-5, 0);
					butterfly.motion(-1.5,0);
				}
				if(pet.x() <= 5)
					pet.nextX(5);
				else if(pet.x() >= canvas.width() - 80)
					pet.nextX(canvas.width() - 80);
				if(butterfly.x() <= 5)
					butterfly.nextX(5);
				else if(butterfly.x() >= canvas.width() - 80)
					butterfly.nextX(canvas.width() - 80);
				butterfly.move();
				pet.move();
			}
			else
			{		
				if(playCounter == 0)
				{
					playCounter = 50;					
					spawn = true;
					butterfly.position((new Random()).nextInt(800), 620);
				}
				else
					playCounter--;
			}		
			pet.checkIfCollidesWith(butterfly);
			if(pet.collided())
			{	
				numberOfCatch--;
				spawn = false;
				butterfly.hide();
				butterfly.motion(0, 0);
				butterfly.position((new Random()).nextInt(800), 0);
				if(butterfly.x() > pet.x())				
					pet.play("standright");			
				else				
					pet.play("standleft");		
				boredom += 5;
				if(boredom >= 100)
					boredom = 100;
			}	
			if(numberOfCatch == 0)
			{
				numberOfCatch = 3;
				heart.hide();
				mode = "wandering";
				butterfly.hide();
				showButton();
				pet.framerate(8);
				pet.motion(0, 0);
			}
			
		}
		public void control()
		{
			if(mode == "sleep")
				sleep();
			else if(mode == "eat")
				eat();
			else if(mode == "wandering")
				wandering();
			else if(mode == "play")
				play();
		}
	}
	private class Food
	{
		private ArrayList<String> description = new ArrayList<String>();		
		private int hungerValue;
		private int healthValue;	
		private int rate;
	}
	public void setup()
	{
		//Initialize window size.
		window.size(1000, 700);
		window.title("Pet");
		
		framerate(25);	
		gameElementInitialize();
		pet = new Animal();		
		statusBarChange();		
		generateFoodChoice();
		startScene("Intro");
	}
	
	
	public void gameElementInitialize()
	{
		feedButton = makeButton("Feed",getImage("../Resources/Image/feedButton.png"),100,84);
		feedButton.position(910, 430);
		playButton = makeButton("Play",getImage("../Resources/Image/playButton.png"),98,84);
		playButton.position(912, 500);
		sleepButton = makeButton("Sleep", getImage("../Resources/Image/sleepButton.png"),95,80);
		sleepButton.position(908, 580);
		wakeButton = makeButton("Wake", getImage("../Resources/Image/wakeButton.png"),95,80);
		wakeButton.position(908, 580);
		wakeButton.hide();
		getFoodList(new File("../Resources/Text/healthy.txt"), new File("../Resources/Text/unhealthy.txt"));
		foodChoices = makeSprite(getImage("../Resources/Image/foodChoices.png"));
		foodChoices.position(250,200);
		foodChoices.hide();
		food1 = makeSprite(100,100);
		food2 = makeSprite(100,100);
		food3 = makeSprite(100,100);		
		food1.hide();		
		food2.hide();
		food3.hide();		
		textBackground = makeSprite(getImage("../Resources/Image/textBackGround.png"));
		textBackground.position(570, 250);
		textBackground.font("Arial", PLAIN, 20, 255, 255, 255);
		good1 = makeSprite(getImage("../Resources/Image/good1.png"));
		good2 = makeSprite(getImage("../Resources/Image/good2.png"));
		good3 = makeSprite(getImage("../Resources/Image/good3.png"));
		textBackground.pin(good1, 15, 10);
		textBackground.pin(good2, 15, 10);
		textBackground.pin(good3, 15, 10);
		bad1 = makeSprite(getImage("../Resources/Image/bad1.png"));
		bad2 = makeSprite(getImage("../Resources/Image/bad2.png"));
		bad3 = makeSprite(getImage("../Resources/Image/bad3.png"));
		textBackground.pin(bad1, 15, 10);
		textBackground.pin(bad2, 15, 10);
		textBackground.pin(bad3, 15, 10);
		hideRating();
		ghost = makeSprite(getImage("../Resources/Image/ghost.png"));
		ghost.position(414,599);
		ghost.motion(0, -0.7);
		ghostRotate = -14;		
		credit = makeSprite( getImage("../Resources/Image/credit.png"));
		credit.position(180, 360);
		introCredit = makeButton("introCredit", getImage("../Resources/Image/intro_credit.png"), 117,37);
		introCredit.position(435, 320);
		introPlay = makeButton("introPlay",getImage("../Resources/Image/intro_play.png"), 342, 43);
		introPlay.position(325, 260);
		credit.hide();
		butterfly = makeSprite(getImage("../Resources/Image/butterfly.png"));
		butterfly.hide();
		mouse.setCursor(mouse.HAND);
	}
	public void screenElementControl()
	{
		pet.pet.draw();	
		hungerBar.draw();
		healthBar.draw();
		boredomBar.draw();
		hungerBar.putText(pet.hunger, 60, 15);
		healthBar.putText(pet.health, 60, 15);
		boredomBar.putText(pet.boredom, 60, 15);
		feedButton.draw();
		playButton.draw();
		sleepButton.draw();
		wakeButton.draw();
		foodChoices.draw();
		food1.draw();
		food2.draw();
		food3.draw();
		butterfly.draw();
		if(pet.mode == "eat" && textDuration > 0)
		{
			textBackground.draw();
			int linePositionHeight = defaultTextY;
			for(String s:foodMap.get(currentFood).description)
			{
				textBackground.putText(s, defaultTextX, linePositionHeight);
				linePositionHeight += 19;
			}	
			textDuration--;
		} 
		else if(timer <= 500 && pet.mode == "sleep" && textDuration > 0)
		{
			textBackground.draw();
			textBackground.putText("Do you know a good night's sleep not",  defaultTextX, defaultTextY); 
			textBackground.putText("only make you feel better,it also makes",  defaultTextX, defaultTextY + 19); 
			textBackground.putText("your thoughts clearer, and your emotions",  defaultTextX, defaultTextY + 38);
			textBackground.putText("are less fragile. Without adequate sleep,",  defaultTextX, defaultTextY + 57);
			textBackground.putText("judgment, mood, and ability to learn ",  defaultTextX, defaultTextY + 76);
			textBackground.putText("and retain information are weakened.",  defaultTextX, defaultTextY + 95);
			textBackground.putText("So try to get at least 7 hours at night.",  defaultTextX, defaultTextY + 114);
			
			good1.show();
			textDuration--;
		}
		else if(timer > 800 && pet.mode == "sleep" && textDuration > 0)
		{   
			textBackground.draw();
			textBackground.putText("While naps do not necessarily make up", defaultTextX, defaultTextY);
			textBackground.putText("for inadequate or poor quality nighttime ", defaultTextX, defaultTextY + 19);
			textBackground.putText("sleep, a shortnap of 20-30 minutes can ", defaultTextX, defaultTextY + 38);
			textBackground.putText("help to improve mood, alertness and ", defaultTextX, defaultTextY + 57);
			textBackground.putText("performance.", defaultTextX, defaultTextY + 76);
			textDuration--;
			good1.show();
		}
		else if(pet.mode == "play" && textDuration > 0)
		{
			textBackground.draw();
			textBackground.putText("Regular exercise can help protect you", defaultTextX, defaultTextY );
			textBackground.putText("from heart disease and stroke, high", defaultTextX, defaultTextY + 19);
			textBackground.putText("blood pressure, and can also improve", defaultTextX, defaultTextY + 38);
			textBackground.putText("your mood and help you to better", defaultTextX, defaultTextY + 57);
			textBackground.putText("manage stress.", defaultTextX, defaultTextY + 76);
			textDuration--; 
			good1.show();
		}
		else
		{
			textDuration = 300;
			hideRating();
		}		
	}
	public void changeDayTime()
	{
		if(0 <= timer && timer <= 500)
			canvas.background(getImage("../Resources/Image/night.png"));
		else if(timer <= 800)
			canvas.background(getImage("../Resources/Image/dawn.png"));
		else if( timer <= 2300)
			canvas.background(getImage("../Resources/Image/noon.png"));
		else if( timer <= 2600)
			canvas.background(getImage("../Resources/Image/evening.png"));
		else
			timer = 0;
		++timer;			
	}
	public void statusBarUpdate()	
	{
		if(counter == 0)
		{
			pet.hunger--;
			pet.health--;
			pet.boredom--;
			counter = 100;	
			statusBarChange();
		}
		counter--;
	}	
	public void statusBarChange()
	{
		if(pet.hunger <= 0)
			pet.hunger = 1;
		if(pet.health <= 0)
			pet.health = 1;
		if(pet.boredom <= 0)
			pet.boredom = 1;
		hungerBar = makeSprite(getImage("../Resources/Image/bar.png"),(int)(pet.hunger * 1.45), 22);
		hungerBar.position(103, 9);		
		hungerBar.font("Arial", BOLD, 12,255,255,255);
		healthBar = makeSprite(getImage("../Resources/Image/bar.png"),(int)(pet.health * 1.45), 22);
		healthBar.position(360, 9);
		healthBar.font("Arial", BOLD, 12,255,255,255);
		boredomBar = makeSprite(getImage("../Resources/Image/bar.png"),(int)(pet.boredom * 1.45), 22);
		boredomBar.position(652, 10);
		boredomBar.font("Arial", BOLD, 12,255,255,255);
	}
	public void generateFoodChoice()
	{
		//Get random food choices
		ArrayList<Sprite> randomList = new ArrayList<Sprite>();
		int ran = new Random().nextInt(unhealthyFoodList.size());
		randomList.add(unhealthyFoodList.get(ran));
		int ran2 = new Random().nextInt(unhealthyFoodList.size());
		while(ran2 == ran)
			ran2 = new Random().nextInt(unhealthyFoodList.size());
		randomList.add(unhealthyFoodList.get(ran2));
		ran = new Random().nextInt(healthyFoodList.size());
		randomList.add(healthyFoodList.get(ran));
		//Shuffle the list
		int index = new Random().nextInt(3);
		Sprite temp = randomList.get(index);		
		randomList.set(index, randomList.get(0));
		randomList.set(0, temp);	
		
		food1 = randomList.get(0);
		food2 = randomList.get(1);
		food3 = randomList.get(2);
		food1.position(310,305);
		food2.position(450,305);
		food3.position(585,305);
		food1.hide();
		food2.hide();
		food3.hide();
	}
	public void onClickFeed()
	{		
		foodChoices.show();
		hideButton();		
		food1.show();
		food2.show();
		food3.show();	
		framerate(1);
		pet.pet.motion(0, 0);
		pet.pet.play("standleft");
	}
	public void onClickPlay()
	{
		hideButton();
		pet.mode = "play";
		spawn = true;
		pet.heart.show();
		pet.pet.framerate(18);
		butterfly.position((new Random()).nextInt(800) + 100, 620);
	}
	public void onClickFoodChoice()
	{
		currentFood = mouse.sprite();		
		pet.hunger += foodMap.get(currentFood).hungerValue;
		pet.health += foodMap.get(currentFood).healthValue;	
		foodChoiceRate = foodMap.get(currentFood).rate;
		showRating();
		if(pet.hunger >= 100)
			pet.hunger = 100;
		if(pet.health >= 100)
			pet.health = 100;
		foodChoices.hide();
		pet.mode = "eat";
		generateFoodChoice();
		framerate(25);
	}
	public void onClickSleep()
	{
		pet.mode = "sleep";
		hideButton();
		wakeButton.show();		
	}
	public void onClickWake()
	{
		showButton();
		wakeButton.hide();		
		pet.sleep.hide();		
		pet.mode = "wandering";
		pet.pet.play("right");
		pet.pet.motion(2, 0);
		pet.sleepCounter = 2000;
	}
	public void getFoodList(File healthyFood, File unhealthyFood)
	{
		try
		{
			Scanner scanner = new Scanner(healthyFood);			
			while (scanner.hasNextLine())
			{
				Scanner sc = new Scanner(scanner.nextLine());			
				Food foodItem = new Food();
				Sprite foodSprite = makeButton("FoodChoice", getImage("../Resources/Image/" + sc.next() +".png"),80, 80);	
				foodItem.hungerValue = sc.nextInt();
				foodItem.healthValue = sc.nextInt();
				foodItem.rate = sc.nextInt();
				Pattern p = Pattern.compile("<end>");
				while(!scanner.hasNext(p))
					foodItem.description.add(scanner.nextLine());
				scanner.nextLine();
				healthyFoodList.add(foodSprite);
				foodMap.put(foodSprite, foodItem);
			}			
			scanner.close();
		}
		catch (IOException e)
		{
			System.out.println("Can not find " + healthyFood);
		}
		
		try
		{
			Scanner scanner = new Scanner(unhealthyFood);			
			while (scanner.hasNextLine())
			{
				Scanner sc = new Scanner(scanner.nextLine());			
				Food foodItem = new Food();				
				Sprite foodSprite = makeButton("FoodChoice", getImage("../Resources/Image/"+ sc.next() +".png"),80, 80);	
				foodItem.hungerValue = sc.nextInt();
				foodItem.healthValue = sc.nextInt();
				foodItem.rate = sc.nextInt();
				Pattern p = Pattern.compile("<end>");
				while(!scanner.hasNext(p))
					foodItem.description.add(scanner.nextLine());	
				scanner.nextLine();
				unhealthyFoodList.add(foodSprite);
				foodMap.put(foodSprite, foodItem);
			}
			scanner.close();
		}
		catch (IOException e)
		{
			System.out.println("Can not find " + unhealthyFood);
		}
	}
	public void hideButton()
	{
		feedButton.hide();
		playButton.hide();
		sleepButton.hide();
		
	}
	public void showButton()
	{
		feedButton.show();
		playButton.show();
		sleepButton.show();
		
	}
	public void showRating()
	{
		if(foodChoiceRate == 3)
			good3.show();
		else if(foodChoiceRate == 2)
			good2.show();
		else if(foodChoiceRate == 1)
			good1.show();
		else if(foodChoiceRate == -3)
			bad3.show();
		else if(foodChoiceRate == -2)
			bad2.show();
		else if(foodChoiceRate == -1)
			bad1.show();
		
		
	}
	public void hideRating()
	{
		good1.hide();
		good2.hide();
		good3.hide();
		bad1.hide();
		bad2.hide();
		bad3.hide();
	}
	public void drawIntro()
	{
		canvas.clear();
		canvas.background(getImage("../Resources/Image/intro.png"));
		credit.draw();
		introCredit.draw();
		introPlay.draw();
	}
	public void drawGameOver()
	{
		canvas.clear();
		canvas.background(getImage("../Resources/Image/end.png"));		
		if(ghostRotate != 90)
			ghostRotate++;
		
		if(ghost.y() >= 470)
			ghost.move();
		if(ghost.x() >= 410)
		ghost.nextX(ghost.x() - 0.5);
		ghost.rotate(ghostRotate);
		ghost.draw();
	}
	
	public void onClickintroCredit()
	{
		credit.show();
	}
	public void onClickintroPlay()
	{		
		startScene("PlayGame");		
	}

	public void drawPlayGame()
	{				
		canvas.clear();
		changeDayTime();
		pet.control();	
		statusBarUpdate();
		screenElementControl();		
		if(pet.health <= 1|| pet.hunger <= 1 || pet.boredom <= 1)
			startScene("GameOver");
	}
}
