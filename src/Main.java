import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.stage.Stage;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Timer;
import java.util.TimerTask;
import javafx.concurrent.Task;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Main extends Application implements Runnable {

  Scene sceneStart, scenePlay;
  private int field_width = 6, field_height = 5;
  private int size = 64;
  private int sposX, sposY;
  private int tempX, tempY;
  ImageView[][] imageBall = new ImageView[5][6];
  Image[] imgBalls = new Image[6];
  Label lb,timeLabel;
  ImageView iv = new ImageView();
  ImageView temp = new ImageView();
  FlowPane flow = new FlowPane();
  AnchorPane ap = new AnchorPane();
  Pane pane = new Pane();
  CombChange u;
  // Media m;
  MediaPlayer goMp, backMp, startMp, playMp, dropMp, pumpedUpMp;
  private int combsoundsNum = 12;
  MediaPlayer[] rmDropMp = new MediaPlayer[combsoundsNum];
  String[] imgFileName = { "image/block_red.png", "image/block_blue.png", "image/block_green.png",
      "image/block_light.png", "image/block_dark.png", "image/block_life.png" };
  private int imageSize = 5;

  private boolean isReleased = false;
  ArrayList<ArrayList<Point>> rmGroup = new ArrayList<ArrayList<Point>>();//
  ArrayList<Integer> rmNumber = new ArrayList<Integer>();

  private long stime = 0;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {

    primaryStage.setTitle("コンボだ山田！！！");
    Button playButton = new Button("始める");
    playButton.setPrefHeight(80);
    playButton.setPrefWidth(115);
    ImageView startIv = new ImageView(new Image("image/start.png"));
    StackPane startSp = new StackPane(startIv, playButton);
    playButton.setOpacity(0.0);
    playButton.setTranslateY(100);
    goMp = new MediaPlayer(new Media(new File("sounds/go.mp3").toURI().toString()));
    backMp = new MediaPlayer(new Media(new File("sounds/back.mp3").toURI().toString()));

    startMp = new MediaPlayer(new Media(new File("sounds/start.mp3").toURI().toString()));
    playMp = new MediaPlayer(new Media(new File("sounds/play.mp3").toURI().toString()));
    startMp.play();
    playButton.setOnAction((ActionEvent) -> {
      backMp.stop();
      startMp.stop();
      goMp.play();
      playMp.play();
      primaryStage.setScene(scenePlay);
      primaryStage.show();
    });

    dropMp = new MediaPlayer(new Media(new File("sounds/drag.mp3").toURI().toString()));
    for (int i = 0; i < combsoundsNum; i++) {
      rmDropMp[i] = new MediaPlayer(
          new Media(new File("sounds/comb" + String.valueOf(i + 1) + ".mp3").toURI().toString()));
    }
    pumpedUpMp = new MediaPlayer(new Media(new File("sounds/pumpedUp.mp3").toURI().toString()));

    pane.setPrefSize((field_width) * size, (field_height) * size);
    for (int i = 0; i < 6; i++) {
      imgBalls[i] = new Image(imgFileName[i]);
    }

    for (int i = 0; i < field_height; i++) {
      for (int j = 0; j < field_width; j++) {
        imageBall[i][j] = new ImageView(imgBalls[(int) (Math.random() * imageSize)]);

        imageBall[i][j].setTranslateX((double) j * size);
        imageBall[i][j].setTranslateY((double) i * size);
      }
      pane.getChildren().addAll(imageBall[i]);
    }
    pane.setBackground(new Background(new BackgroundFill(Color.rgb(53	, 29	, 20	), CornerRadii.EMPTY, Insets.EMPTY)));
    // pane.setBackground(new Background(new BackgroundFill(Color.rgb( 66, 38,31	), CornerRadii.EMPTY, Insets.EMPTY)));

    EventHandler<MouseDragEvent> mouseDrag = (event) -> this.mouseDrag(event);
    pane.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER, mouseDrag);

    EventHandler<MouseDragEvent> mouseReleased = (event) -> this.mouseReleased(event);
    EventHandler<MouseEvent> mouseDragDetected = (event) -> this.mouseDetected(event);
    for (int i = 0; i < field_height; i++) {
      for (int j = 0; j < field_width; j++) {
        imageBall[i][j].addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, mouseReleased);
        imageBall[i][j].addEventHandler(MouseEvent.DRAG_DETECTED, mouseDragDetected);
      }
    }
    timeLabel = new Label("残り10秒");
    lb = new Label("0コンボ");
    Button backButton = new Button("もどる");
    backButton.setOnAction((ActionEvent) -> {
      goMp.stop();
      playMp.stop();
      backMp.play();
      startMp.play();
      primaryStage.setScene(sceneStart);
      primaryStage.show();
    });
    // FlowPane backFp = new FlowPane();
    VBox vBox = new VBox();
    vBox.setAlignment(Pos.CENTER);
    vBox.setPadding(new Insets(10, 10, 10, 10));
    vBox.getChildren().addAll(timeLabel,lb, backButton);
    BorderPane playBp = new BorderPane();
    playBp.setLeft(pane);
    playBp.setRight(vBox);

    scenePlay = new Scene(playBp);

    sceneStart = new Scene(startSp, 384, 640);
    // sceneStart.setFill(startIv);
    primaryStage.setScene(sceneStart);
    // primaryStage.setScene(scenePlay);
    primaryStage.show();

  }

  @Override
  public void run() {
    // int
    // final Handler handler = new Handler();
    // final int cmc = 0;
    int combCnt = 0;
    while (rmNumber.size() != 0) {

      final int cmc = rmNumber.size();
      final int combc = combCnt;
      final ExecutorService service = Executors.newFixedThreadPool(5);
      final Thread thread = new Thread(() -> changeLabel(lb, combc, combc + cmc));
      service.execute(thread);
      service.shutdown();

      for (int r = 0; r < rmNumber.size(); r++) {
        for (int rn = 0; rn < rmGroup.get(rmNumber.get(r)).size(); rn++) {
          int x = rmGroup.get(rmNumber.get(r)).get(rn).x;
          int y = rmGroup.get(rmNumber.get(r)).get(rn).y;
          imageBall[y][x].setImage(null);

        }
        try {
          int sc = (combCnt + r>=combsoundsNum-1)?combsoundsNum-1: combCnt + r;
          rmDropMp[sc].play();
          Thread.sleep(500);
          rmDropMp[sc].stop();
        } catch (Exception e) {
          // TODO: handle exception
        }
      }

      for (int j = 0; j < field_width; j++) {// 落とす．
        int d = field_height - 1;
        for (int i = field_height - 2; i >= 0; i--) {
          if (imageBall[d][j].getImage() == null) {
            if (imageBall[i][j].getImage() != null) {
              imageBall[d][j].setImage(imageBall[i][j].getImage());
              imageBall[i][j].setImage(null);
              d--;
            }
          } else {
            d--;
          }
        }
      }
      try {
        Thread.sleep(100);
      } catch (Exception e) {
        // TODO: handle exception
      }
      for (int i = 0; i < field_height; i++) {
        for (int j = 0; j < field_width; j++) {
          if (imageBall[i][j].getImage() == null) {
            imageBall[i][j].setImage(imgBalls[(int) (Math.random() * imageSize)]);
          }
        }
      }
      try {
        Thread.sleep(300);
      } catch (Exception e) {
        // TODO: handle exception
      }
      combCnt += rmNumber.size();
      rmGroup.clear();
      rmNumber.clear();
      if (checkBall(rmNumber, rmGroup) != 0)
        continue;
      // break;

      for (int r = 0; r < combCnt; r++) {
        try {
          pumpedUpMp.play();
          Thread.sleep(500);
          pumpedUpMp.stop();
        } catch (Exception e) {
          // TODO: handle exception
        }
      }
      break;

    }

  }

  private void mouseDetected(MouseEvent event) {
    System.out.println("drag detected");
    // // System.out.println(getBoundsInParent());
    sposX = (int) event.getSceneX() / size;
    sposY = (int) event.getSceneY() / size;
    System.out.println(sposX + ":" + sposY);
    // アクティブサークルの設定
    iv = (ImageView) event.getSource();
    iv.toFront();
    stime = System.currentTimeMillis();
    timeLabel.setText("残り10秒");
    // System.out.println(iv);
    // ドラッグ中の色とマウス形状を変更
    // iv.setBlendMode(BlendMode.OVERLAY);
    // scenePlay.setCursor(Cursor.CLOSED_HAND);
    // ドラッグ開始
    // System.out.println("クリックしています．");
    iv.startFullDrag();
    event.consume();
  }

  private void mouseReleased(MouseDragEvent event) {
    System.out.println("mouse released");
    event.consume();
    // // サークルの色とマウスの形状を元に戻す
    // scenePlay.setCursor(Cursor.DEFAULT);
    iv.setTranslateX(((int) event.getSceneX() / size) * size);
    iv.setTranslateY(((int) event.getSceneY() / size) * size);
    imageBall[(int) event.getSceneY() / size][(int) event.getSceneX() / size] = iv;

    rmGroup.clear();
    rmNumber.clear();
    if (checkBall(rmNumber, rmGroup) != 0) {
      (new Thread(this)).start();// ballの削除処理開始
    }
  }

  private void changeLabel(Label label, int min, int max) {
    try {
      for (int i = min; i < max; i++) {
        final int c = i;
        Platform.runLater(() -> label.setText(String.valueOf(c + 1 + "コンボ")));
        Thread.sleep(500);
      }
    } catch (final InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void mouseDrag(MouseEvent e) {
    Pane p = (Pane) e.getSource();
    // temp=(ImageView)e.getSource();
    timeLabel.setText("残り"+String.valueOf((10000 - (System.currentTimeMillis() - stime)) / 1000)+"秒");
    if (p.getLayoutBounds().contains(e.getX() + 1, e.getY() + 1) == false
    // if (System.currentTimeMillis() - stime >= 10000 ||p.getLayoutBounds().contains(e.getX() + 1, e.getY() + 1) == false
        || p.getLayoutBounds().contains(e.getX() + 1, e.getY() - 1) == false
        || p.getLayoutBounds().contains(e.getX() - 1, e.getY() + 1) == false
        || p.getLayoutBounds().contains(e.getX() - 1, e.getY() - 1) == false) {
      int x = (int) e.getX() / size;
      int y = (int) e.getY() / size;
      if (x < 0)
        x = 0;
      else if (field_width <= x)
        x = field_width - 1;
      if (y < 0)
        y = 0;
      else if (field_height <= y)
        y = field_height - 1;
      iv.setTranslateX(x * size);
      iv.setTranslateY(y * size);
      imageBall[y][x] = iv;
      // imageBall[sposY][sposX].removeEventHandler(MouseEvent.DRAG_DETECTED, this);
      return;
    } else {
      tempX = (int) e.getX() / size;
      tempY = (int) e.getY() / size;
      if ((tempX != sposX || tempY != sposY)) {
        dropMp.stop();
        // imageBall[sposY][sposX].setImage(imageBall[tempY][tempX].getImage());
        imageBall[tempY][tempX].setTranslateX(sposX * size);
        imageBall[tempY][tempX].setTranslateY(sposY * size);
        imageBall[sposY][sposX] = imageBall[tempY][tempX];
        sposX = tempX;
        sposY = tempY;
        dropMp.play();
      }
      e.consume();

      iv.setTranslateX(e.getX() - size / 2);
      iv.setTranslateY(e.getY() - size / 2);
    }

  }

  private int checkBall(ArrayList<Integer> rmNumber, ArrayList<ArrayList<Point>> rmGroup) {
    int[][] groupNum = new int[field_height][field_width];// グループ番号

    int gpNum = 0;

    // グループ番号の初期化
    for (int i = 0; i < field_height; i++) {
      for (int j = 0; j < field_width; j++) {
        groupNum[i][j] = -1;
      }
    }
    int seqCnt = 1;

    // 横方向からチェック
    for (int i = field_height - 1; i >= 0; i--) {
      seqCnt = 1;
      for (int j = 0; j < field_width - 1; j++) {

        if (imageBall[i][j].getImage().equals(imageBall[i][j + 1].getImage())) {
          seqCnt++;

        } else {
          if (seqCnt >= 3) {
            ArrayList<Point> coordinate = new ArrayList<Point>();// ①に格納する．
            int k = j;
            while (seqCnt > 0) {
              groupNum[i][k] = gpNum;// グループ番号
              coordinate.add(new Point(k, i));// 座標
              k--;
              seqCnt--;
            }
            rmGroup.add(coordinate);
            gpNum++;
          }
          seqCnt = 1;
        }

        if (field_width / 2 <= j && seqCnt == 1)
          break;// ３つ連続で繋がることがないので調べる必要がない
      }
      if (seqCnt != 1) {
        ArrayList<Point> coordinate = new ArrayList<Point>();// ①に格納する．
        int k = field_width - 1;
        while (seqCnt > 0) {
          groupNum[i][k] = gpNum;// グループ番号
          coordinate.add(new Point(k, i));// 座標
          k--;
          seqCnt--;
        }
        rmGroup.add(coordinate);
        gpNum++;
        seqCnt = 1;
      }
    }
    seqCnt = 1;
    // 縦方向（＊＊＊横方向と同等に調べていくが，同じグループになりそうなグループをまとめる．後に結合させていく***)
    for (int j = 0; j < field_width; j++) {
      seqCnt = 1;
      for (int i = field_height - 1; i > 0; i--) {
        if (imageBall[i][j].getImage().equals(imageBall[i - 1][j].getImage())) {
          seqCnt++;
        } else {
          if (seqCnt >= 3) {
            ArrayList<Point> coordinate = new ArrayList<Point>();// ①に格納する．
            int k = i;
            while (seqCnt > 0) {

              if (groupNum[k][j] >= 0) {
                int n = groupNum[k][j];
                for (int m = 0; m < rmGroup.get(n).size(); m++) {
                  int x = rmGroup.get(n).get(m).x;
                  int y = rmGroup.get(n).get(m).y;
                  groupNum[y][x] = gpNum;
                }
                coordinate.addAll(rmGroup.get(n));
              } else {
                groupNum[k][j] = gpNum;// グループ番号
                coordinate.add(new Point(j, k));// 座標
              }
              k++;
              seqCnt--;
            }
            rmGroup.add(coordinate);
            gpNum++;
          }
          seqCnt = 1;
        }

        if (field_height / 2 >= i && seqCnt == 1)
          break;// ３つ連続で繋がることがないので調べる必要がない
      }
      if (seqCnt != 1) {
        ArrayList<Point> coordinate = new ArrayList<Point>();// ①に格納する．
        int k = 0;
        while (seqCnt > 0) {
          if (groupNum[k][j] >= 0) {
            int n = groupNum[k][j];
            for (int m = 0; m < rmGroup.get(n).size(); m++) {
              int x = rmGroup.get(n).get(m).x;
              int y = rmGroup.get(n).get(m).y;
              groupNum[y][x] = gpNum;
            }
            coordinate.addAll(rmGroup.get(n));
          } else {
            groupNum[k][j] = gpNum;// グループ番号
            coordinate.add(new Point(j, k));// 座標
          }
          k++;
          seqCnt--;
        }
        rmGroup.add(coordinate);
        gpNum++;
        seqCnt = 1;
      }
    }

    // 縦２横3以上のグループを一緒にする．
    for (int j = 0; j < field_width; j++) {
      for (int i = field_height - 1; i > 0; i--) {
        if (groupNum[i][j] >= 0 && groupNum[i - 1][j] >= 0
            && imageBall[i][j].getImage().equals(imageBall[i - 1][j].getImage())) {
          int n = groupNum[i - 1][j];
          int b = groupNum[i][j];
          for (int m = 0; m < rmGroup.get(n).size(); m++) {
            int x = rmGroup.get(n).get(m).x;
            int y = rmGroup.get(n).get(m).y;
            groupNum[y][x] = b;
          }
          rmGroup.get(b).addAll(rmGroup.get(n));
        }
      }
    }

    boolean[] rmChecked = new boolean[gpNum];// 削除済みグループ
    for (int i = 0; i < gpNum; i++) {
      rmChecked[i] = false;
    }
    for (int i = field_height - 1; i >= 0; i--) {
      for (int j = 0; j < field_width; j++) {
        if (groupNum[i][j] >= 0 && !rmChecked[groupNum[i][j]]) {
          rmChecked[groupNum[i][j]] = true;
          rmNumber.add(groupNum[i][j]);
        }
      }
    }
    return rmNumber.size();
  }

}
