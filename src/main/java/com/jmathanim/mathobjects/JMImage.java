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
import javafx.scene.image.Image;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMImage extends AbstractJMImage {

	private String filename;

	public static JMImage make(String filename) {
		return new JMImage(filename);
	}

	private final JavaFXRenderer renderer;

	public JMImage(String filename) {
		setCached(true);
		this.filename = filename;
		renderer = (JavaFXRenderer) JMathAnimConfig.getConfig().getRenderer();
		this.bbox = renderer.createImage(filename);
		double sc = renderer.getMediaHeight() * 1d / 1080d;// Scales it taking as reference 1920x1080 production output
		this.scale(sc);
	}

	public void setImage(String fn) {
		Rect bb = renderer.createImage(fn);
		bb.centerAt(this.bbox.getCenter());
		this.filename = fn;
	}

	@Override
	public JMImage copy() {
		JMImage resul = new JMImage(filename);
		resul.bbox.copyFrom(this.bbox);
		resul.getMp().copyFrom(this.getMp());
		resul.preserveRatio = this.preserveRatio;
		resul.rotateAngle = this.rotateAngle;
		resul.rotateAngleBackup = this.rotateAngleBackup;
		return resul;
	}

    @Override
    public void copyStateFrom(MathObject obj) {
        if (!(obj instanceof JMImage)) return;
        JMImage img=(JMImage)obj;
        bbox.copyFrom(img.bbox);
        getMp().copyFrom(img.getMp());
        preserveRatio=img.preserveRatio;
        rotateAngle=img.rotateAngle;
        rotateAngleBackup=img.rotateAngleBackup;
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
