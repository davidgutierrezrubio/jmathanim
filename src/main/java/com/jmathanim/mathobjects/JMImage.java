/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMImage extends AbstractJMImage {

    private String filename;

    public static JMImage make(String filename) {
        try {
            URL url=new URL(filename);
             return new JMImage(url.openStream());
        } catch (MalformedURLException ex) {
            JMathAnimScene.logger.error("Malformed url for image "+filename);
        } catch (IOException ex) {
            JMathAnimScene.logger.error("I/O error reading image "+filename);
        }
       return null;
    }
    
    private final JavaFXRenderer renderer;
    private final InputStream stream;

    public JMImage(InputStream stream) {
        this.stream=stream;
        setCached(true);
        this.filename = stream.toString();
        renderer = (JavaFXRenderer) JMathAnimConfig.getConfig().getRenderer();
        this.bbox = renderer.createImage(stream);
        double sc = renderer.getMediaHeight() * 1d / 1080d;// Scales it taking as reference 1920x1080 production output
        this.scale(sc);
    }

    public void setImage(String fn) {
        try {
            Rect bb = renderer.createImage(new URL(fn).openStream());
            bb.centerAt(this.bbox.getCenter());
            this.filename = fn;
        } catch (MalformedURLException ex) {
            Logger.getLogger(JMImage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JMImage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public JMImage copy() {
        JMImage resul = new JMImage(this.stream);
        resul.bbox.copyFrom(this.bbox);
        resul.getMp().copyFrom(this.getMp());
        resul.preserveRatio = this.preserveRatio;
        resul.rotateAngle = this.rotateAngle;
        resul.rotateAngleBackup = this.rotateAngleBackup;
        return resul;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        if (!(obj instanceof JMImage)) {
            return;
        }
        JMImage img = (JMImage) obj;
        bbox.copyFrom(img.bbox);
        getMp().copyFrom(img.getMp());
        preserveRatio = img.preserveRatio;
        rotateAngle = img.rotateAngle;
        rotateAngleBackup = img.rotateAngleBackup;
    }

    @Override
    public void update(JMathAnimScene scene) {
    }

    @Override
    public void restoreState() {
        super.restoreState();
        bbox.restoreState();
        this.rotateAngle = this.rotateAngleBackup;
    }

    @Override
    public void saveState() {
        super.saveState();
        bbox.saveState();
        this.rotateAngleBackup = this.rotateAngle;
    }

    @Override
    public <T extends MathObject> T scale(Point scaleCenter, double sx, double sy, double sz) {
        bbox.copyFrom(
                Rect.make(bbox.getUL().scale(scaleCenter, sx, sy, sz), bbox.getDR().scale(scaleCenter, sx, sy, sz)));
        return (T) this;
    }

    @Override
    public <T extends MathObject> T rotate(Point center, double angle) {
        Point centerBbox = bbox.getCenter();
        centerBbox.rotate(center, angle);
        bbox.copyFrom(bbox.shifted(bbox.getCenter().to(centerBbox)));
        // For now, ignore rotate center
        rotateAngle += angle;
        return (T) this;
    }

    /**
     * Place the image adequately shifting, rotating and scaling so that lower
     * corners lie in given points
     *
     * @param A Lower left corner of image
     * @param B Lower right corner of image
     * @return This object
     */
    public JMImage adjustTo(Point A, Point B) {
        rotate(-rotateAngle);
        double w = getBoundingBox().getWidth();
        Point D = getBoundingBox().getDL();
        shift(D.to(A));
        rotate(A, A.to(B).getAngle());
        double sc = A.to(B).norm() / w;
        scale(A, sc, sc);
        return this;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String getId() {
        return getFilename();
    }

    @Override
    public Image getImage() {
        return renderer.getImageFromCatalog(this);
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform tr) {
        // Nothing to do (for now...)
        return (T) this;
    }
}
