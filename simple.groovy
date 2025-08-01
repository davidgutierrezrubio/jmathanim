import com.jmathanim.Animations.PlayAnim
import com.jmathanim.Utils.JMathAnimConfig
import com.jmathanim.jmathanim.Scene2D
import com.jmathanim.mathobjects.Arrow
import com.jmathanim.mathobjects.Point
import com.jmathanim.mathobjects.Shape
import groovy.transform.Field
import javafx.scene.Camera

//**************************************
//These declarations are needed only for
// intellij autocompletion and they are
// ignored by JMathAnim. You can safely
// delete them, but autocompletion may
// not work properly.
@Field Scene2D scene=null
@Field Camera camera=null
@Field Camera fixedCamera=null
@Field JMathAnimConfig config=null
@Field PlayAnim play=null
//**************************************

def setupSketch()
{
    config.setLoggingLevel(4)
    config.parseFile("#light.xml")
    config.parseFile("#preview.xml")
    config.setLimitFPS(true)
    config.setDefaultLambda {t->t}
}
def c = Shape.circle()
def s= Shape.square().center()
Arrow ar=Arrow.make(Point.at(1,1),Point.at(-1,0), Arrow.ArrowType.ARROW1)
//ar.setCurvature(PI/4)
ar.addLengthLabel(.1,"0.00")
play.showCreation(1,ar)
scene.waitSeconds(3);