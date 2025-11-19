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
package com.jmathanim.MathObjects;

import com.jmathanim.Renderers.FXRenderer.JavaFXRenderer;
import com.jmathanim.Utils.*;
import com.jmathanim.jmathanim.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMImage extends AbstractJMImage<JMImage> {

    private final JavaFXRenderer renderer;
    private final InputStream stream;
    private String filename;
    public JMImage(InputStream stream) {
        super();
        renderer = (JavaFXRenderer) JMathAnimConfig.getConfig().getRenderer();
        this.stream = stream;
        if (stream == null){
            this.bbox = EmptyRect.make();
        }
        else {
            setCached(true);
            this.filename = stream.toString();
            this.bbox = renderer.createImage(stream);
            double sc = renderer.getMediaHeight() * 1d / 1080d;// Scales it taking as reference 1920x1080 production output
        }
//        this.scale(sc);
    }

    public static JMImage make(String filename) {
        try {
            ResourceLoader rl = new ResourceLoader();
            URL urlImage = rl.getExternalResource(filename, "images");
            return new JMImage(urlImage.openStream());
        } catch (MalformedURLException ex) {
            JMathAnimScene.logger.error("Malformed url for image " + filename);
        } catch (IOException ex) {
            JMathAnimScene.logger.error("I/O error reading image " + filename);
        }
        return new JMImage(null);
    }

    @Override
    public JMImage copy() {
        JMImage resul = new JMImage(this.stream);
        resul.copyStateFrom(this);
        return resul;
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof JMImage)) {
            return;
        }
        super.copyStateFrom(obj);
        JMImage img = (JMImage) obj;
        bbox.copyFrom(img.bbox);
        preserveRatio = img.preserveRatio;
        this.currentViewTransform.copyFrom(img.currentViewTransform);
        changeVersion();
    }

    @Override
    public void performMathObjectUpdateActions() {

    }

    /**
     * Place the image adequately shifting, rotating and scaling so that lower corners lie in given points
     *
     * @param A Lower left corner of image
     * @param B Lower right corner of image
     * @return This object
     */
    public JMImage adjustTo(Coordinates A, Coordinates B) {

        Vec origA = bbox.getLowerLeft();
        Vec origB = bbox.getLowerRight();
        currentViewTransform = AffineJTransform.createDirect2DIsomorphic(origA, origB, A, B, 1);
        return this;
    }


//    @Override
//    public <T extends MathObject> T scale(Point scaleCenter, double sx, double sy, double sz) {
//        bbox.copyFrom(
//                Rect.makeLengthMeasure(bbox.getUL().scale(scaleCenter, sx, sy, sz), bbox.getDR().scale(scaleCenter, sx, sy, sz)));
//        return (T) this;
//    }

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

}
