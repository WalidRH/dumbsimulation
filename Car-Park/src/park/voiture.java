package park;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class voiture implements Runnable {
	String Nom;
	long time_in_park;
	TimeUnit unit;
	Semaphore sempark;
	Semaphore semrampe;
        Semaphore semRoad;
        JPanel panel;
        BufferedImage car,hide,carexit,carUP;
        boolean [] numPlace = new boolean[3];
        int X=0;
        int Y=300;
        int Dx,numero;
        voiture vp;
        Graphics carp,surface;
	public voiture(String nom, long time_in_park,int disstance, TimeUnit unit, Semaphore semP, Semaphore semR, Semaphore semraod,JPanel panel1, voiture v, int indice) {
                Nom = nom;
                this.time_in_park = time_in_park;
                this.unit = unit;
                this.sempark = semP;
                this.semrampe = semR;
                this.semRoad = semraod;
                this.panel = panel1;
                this.Dx = disstance;
                this.vp = v;
                this.numPlace[indice] = true;
                this.numero = indice;
                
            try {
                
                car = ImageIO.read(new File("src/car.png"));
                carexit = ImageIO.read(new File("src/cars.png"));
                carUP = ImageIO.read(new File("src/carUP.png"));
                hide = ImageIO.read(new File("src/hide.png"));
                
                paintCar();
            } catch (IOException ex) {
                Logger.getLogger(voiture.class.getName()).log(Level.SEVERE, null, ex);
            }
            
	}

   
    public void paintCar() {
        
        System.out.println("Drawing Car");
         carp = panel.getGraphics();
        carp.drawImage(car, X, 300, 100, 50,null);
        
    }
      // move cars  
    public void move(int dx){
      surface = panel.getGraphics(); 
      surface.setColor(Color.DARK_GRAY);
    surface.fillRect( X, Y, 100, 50);
    X+=dx;
    surface.drawImage(car, X, Y, 100, 50,null);
   
    }
    // move car in opposite way
    public void moveExit(int dx){
        surface = panel.getGraphics(); 
        surface.setColor(Color.DARK_GRAY);  
       
    surface.fillRect( X, Y, 100, 50);
    X-=dx;
    surface.drawImage(carexit, X, Y, 100, 50,null);
    
    }
    // move the car to park
    public void TakePlaceUP(int dy){
         surface = panel.getGraphics(); 
         surface.setColor(Color.DARK_GRAY);
        surface.fillRect( X, Y, 50, 100);
        Y-=dy;
        surface.drawImage(carUP, X, Y, 50, 100,null);
        
    }
    // 
    public void ExitPlaceUP(int dy){
        surface = panel.getGraphics(); 
         surface.setColor(Color.DARK_GRAY);
        surface.fillRect( X, Y, 50, 100);
        Y+=dy;
        surface.drawImage(carUP, X, Y, 50, 100,null);
        
    }
    // next car
    public void next(){
           
            System.err.println("HERE");
            for (int i = Dx; i < Dx+100; i++) {
                this.move(1);
            }
            this.setDx(Dx+=100);
            System.err.println("Dx " +Dx);
            
    }
    // la queue
    public void line(){
    if ( vp != null && vp.getDx()-this.getDx() == 200 ){
        for (int i = this.getDx(); i < vp.getDx()-100; i++) {
            move(1);
        }
        this.setDx( Dx = Dx + 100);
        System.err.println("new Dx "+Dx);
    }
}
    	// search the pos for next car
   public int searchPos(int nbr){
       int numPlace = 0;
       if(nbr < this.numero && vp != null){
           vp.searchPos(nbr++);
       
       if (vp.numPlace[nbr] != true)
             numPlace = nbr;
           else
               numPlace = -1;
       if(numPlace != -1 ){
           return numPlace;
       }
       }
       return -1;   
   }
   //check collision
   public void collision(){
	   
   }
	@Override
	public void run() {
		// TODO Auto-generated method stub
                System.out.println(""+this.getNom()+" going to park");
                
                this.Road();
                
                System.out.println(""+this.getNom()+" want to park");
		this.EnterPark();
                
		try {
			this.getUnit().sleep( this.getTime_in_park() );
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                
		this.exitPark(time_in_park);
              
		
	}
        private void Road(){
            try {
                for (int i = 0; i < Dx; i++) {
                    move(1);
                    try {
			this.getUnit().sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            }
                System.err.println(""+Dx);
                while(this.getDx() <= 500){
                try {
                Thread.sleep(100);
                this.line();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(voiture.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                this.getSemRoad().acquire();
               if (Dx < 700) {
                        this.next();
                    }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(voiture.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
	private void EnterPark() {
		// TODO Auto-generated method stub
		try {
			this.getSempark().acquire();
                        System.err.println("TAKEN");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(this.getNom()+" a donner sa carte : ATTENTE");
		try {
			this.getUnit().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
            System.out.println( this.getNom() + " a fini son attente");
        }
                
		try {
                       
                        this.getSemrampe().acquire();
                        for (int i = 0; i < 100; i++) {
                            move(1);
                            try {
			this.getUnit().sleep(10);
                                } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                }
                        }
                    this.getSemRoad().release();
                     System.err.println("Raod Realised");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
               
		System.out.println( this.getNom() + " passe sur la rampe");
                
                  int numP = this.searchPos(0);
              
                    
		this.getSemrampe().release();
		System.out.println( this.getNom() + " est descendu de la rampe et est garé a sa place !");
                surface = panel.getGraphics(); 
                
                switch (numP){
                    case -1 :  if (vp == null) {
			                    	 surface.setColor(Color.DARK_GRAY);
			                         surface.fillRect( X, Y, 200, 50);
                                     this.X+=50;
                                     this.Y-=50;
                                   for (int i = 0; i < 100; i++) {
                                       this.TakePlaceUP(1);
                                   try {
                                       this.getUnit().sleep(10);
                                       } catch (InterruptedException e) {
                                               // TODO Auto-generated catch block
                                               e.printStackTrace();
                                       }
                                                }
                            }else{
                                   if (this.vp.numero == 0){
                                	   surface.setColor(Color.DARK_GRAY);
  			                         surface.fillRect( X, Y, 200, 50);
                                       for (int i = 0; i < 200; i++) {
                                            move(1);
                                            try {
                                                this.getUnit().sleep(10);
                                                } catch (InterruptedException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                }
                                           }
                                       }else if (this.vp.numero == 1){
                                    	   surface.setColor(Color.DARK_GRAY);
      			                         surface.fillRect( X, Y, 200, 50);
                                           for (int i = 0; i < 400; i++) {
                                            move(1);
                                            try {
                                                this.getUnit().sleep(10);
                                                } catch (InterruptedException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                }
                                           }
                                       }
                                   surface.setColor(Color.DARK_GRAY);
			                         surface.fillRect( X, Y, 200, 50);
                                       this.X+=50;
                                     this.Y-=50;
                                   for (int i = 0; i < 100; i++) {
                                       this.TakePlaceUP(1);
                                   try {
                                       this.getUnit().sleep(10);
                                       } catch (InterruptedException e) {
                                               // TODO Auto-generated catch block
                                               e.printStackTrace();
                                       }
                                                }
                                   
                                }break;
                        case 0 : surface.setColor(Color.DARK_GRAY);
                        surface.fillRect( X, Y, 200, 50);  
                        	this.X+=50;
                                     this.Y-=50;
                                   for (int i = 0; i < 100; i++) {
                                       this.TakePlaceUP(1);
                                   try {
                                       this.getUnit().sleep(10);
                                       } catch (InterruptedException e) {
                                               // TODO Auto-generated catch block
                                               e.printStackTrace();
                                       }
                                                }break;
                            
                        case 1: 
                        	for (int i = 0; i < 200; i++) {
                                            move(1);
                                            try {
                                                this.getUnit().sleep(10);
                                                } catch (InterruptedException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                }
                                           }
                        	 surface.setColor(Color.DARK_GRAY);
	                         surface.fillRect( X, Y, 200, 50);
                                 this.X+=50;
                                     this.Y-=50;
                                   for (int i = 0; i < 100; i++) {
                                       this.TakePlaceUP(1);
                                   try {
                                       this.getUnit().sleep(10);
                                       } catch (InterruptedException e) {
                                               // TODO Auto-generated catch block
                                               e.printStackTrace();
                                       }
                                                }break;
                        case 2 : for (int i = 0; i < 400; i++) {
                                            move(1);
                                            try {
                                                this.getUnit().sleep(10);
                                                } catch (InterruptedException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                }
                                           }
                        surface.setColor(Color.DARK_GRAY);
                        surface.fillRect( X, Y, 200, 50);
                                 this.X+=50;
                                     this.Y-=50;
                                   for (int i = 0; i < 100; i++) {
                                       this.TakePlaceUP(1);
                                   try {
                                       this.getUnit().sleep(10);
                                       } catch (InterruptedException e) {
                                               // TODO Auto-generated catch block
                                               e.printStackTrace();
                                       }
                                                }break;
                        
                }
                    
           }
	public void exitPark(long time){
		
			try {
				this.getSemrampe().acquire();
				 
                             for (int i = 0; i < 100; i++) {
                                this.ExitPlaceUP(1);
                                try {
                                            this.getUnit().sleep(10);
                                    } catch (InterruptedException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                    }
                                     }
                                   
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println( this.getNom() + " passe sur la rampe (pour sortir)");
                            this.getSemrampe().release();
                           surface = panel.getGraphics(); 
                         surface.setColor(Color.DARK_GRAY);
                         surface.fillRect( X, Y, 50, 100);
                          this.X-=50;
                          this.Y=300;
                          for (int i = 0; i < 500; i++) {
                            move(1);
                            try {
			this.getUnit().sleep(10);
                                    } catch (InterruptedException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                    }
                        }
                          surface = panel.getGraphics(); 
                         surface.setColor(Color.DARK_GRAY);
                          surface.fillRect( X, Y, 100, 50);
                          Y = 352;
                        
			System.out.println( this.getNom() + " est descendu de la rampe (pour sortir)");
			this.getSempark().release();
                        this.numPlace[numero] = false;
			System.out.println( this.getNom() + " est sorti du parking...Place liberée !");
                        for (int i = 0; i < 2000; i++) {
                            moveExit(1);
                            try {
			this.getUnit().sleep(10);
                            } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                            }
                        }
		
	}

	public String getNom() {
		return Nom;
	}

	public void setNom(String nom) {
		Nom = nom;
	}

	public long getTime_in_park() {
		return time_in_park;
	}

	public void setTime_in_park(long time_in_park) {
		this.time_in_park = time_in_park;
	}

	public TimeUnit getUnit() {
		return unit;
	}

	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}

	public Semaphore getSempark() {
		return sempark;
	}

	public void setSempark(Semaphore sem) {
		this.sempark = sem;
	}

	public Semaphore getSemrampe() {
		return semrampe;
	}

	public void setSemrampe(Semaphore semrampe) {
		this.semrampe = semrampe;
	}

    public Semaphore getSemRoad() {
        return semRoad;
    }

    public void setSemRoad(Semaphore semRoad) {
        this.semRoad = semRoad;
    }

    public int getDx() {
        return Dx;
    }

    public void setDx(int Dx) {
        this.Dx = Dx;
    }

   
        
}
