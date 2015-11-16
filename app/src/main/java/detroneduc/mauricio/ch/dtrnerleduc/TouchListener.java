package detroneduc.mauricio.ch.dtrnerleduc;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by MauricioF on 18.09.2014.
 */
public class TouchListener implements View.OnTouchListener {
    private Player player;
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPosPlayer(int x, int y){
        player.setY(y);
        player.setX(x);
    }



    @Override
    public boolean onTouch(View view, MotionEvent event) {

            player.setY((int) event.getY());
            player.setX((int) event.getX());
            return true;
    }
}
