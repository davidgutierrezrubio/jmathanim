/*
 * Copyright (C) 2021 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.mathobjects.surface;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class HalfEdge {
    public int toVertex=-1;
    public int face=-1;
    public int thisEdge=-1;
    public int oppositeEdge=-1;
    public int nextEdge=-1;

    public HalfEdge(int toVertex,int face,int oppositeEdge, int nextEdge) {
        this.toVertex=toVertex;
        this.face=face;
        this.oppositeEdge=oppositeEdge;
        this.nextEdge=nextEdge;
    }

    
}
