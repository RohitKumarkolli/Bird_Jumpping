import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 360;
    int boardHeiht =640;
    
//images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;


    //Bird
    int birdX = boardWidth/8;
    int birdY = boardHeiht/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img =img;
        }
    }
    //pipes
    int pipeX = boardHeiht;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight =512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }

    }

    // game logic
    Bird bird;
    int velocityX = -4; //moment of the pipes on the screen from right to left
    int velocityY = 0; 
    int gravity = 1;

    ArrayList<Pipe> pipes ; 
    Random random = new Random();


    Timer gameLoop;
    Timer placePipTimer;
    boolean gameOver = false;
    double score =0;

    FlappyBird(){
        
        setPreferredSize(new Dimension(boardWidth,boardHeiht));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load image
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipe timer
        placePipTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        placePipTimer.start();

        //game timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePipes() {
        //(0-1) * pipeHeight/2 -> (0-256)
        //128
        //0 - 128 - (0-256) --> 1/4 pipeHeight -> 3/4 pipeHeight

        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingspace = boardHeiht/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingspace;
        pipes.add(bottomPipe);
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g );
        draw(g);
    }

    //draw
    public void draw(Graphics g){
        //backGround
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeiht, null);

        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null );
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver){
            g.drawString("GAME OVER : " + String.valueOf((int) score), 10, 35);
        }
        else{
            g.drawString("SCORE : "+String.valueOf((int) score), 10, 35);
        }
    }

    public void move(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipe
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;   //0.5 beause there are 2 pipes 
            }


            if (collision(bird, pipe)){
                gameOver = true;
            }
        } 

        if (bird.y > boardHeiht) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width &&       //a's top left corner doesent reaches b's top right corner
                a.x + a.width > b.x &&      //a's top right corner passes b's top left corner
                a.y < b.y + b.height &&     //a's top left corner doesent reaches b's top left corner
                a.y + a.height > b.y;       //a's top left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) {
                //restart game
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
