package jgpfun.world2d.senses;

import jgpfun.world2d.Body2d;

/**
 *
 * @author hansinator
 */
public class GpsSense {

    private final Body2d body;


    public GpsSense(Body2d body) {
        this.body = body;
    }

    public double getX() {
        return body.x;
    }

    public double getY() {
        return body.y;
    }
}