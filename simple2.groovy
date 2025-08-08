void setupSketch() {
    config.setLoggingLevel(4)//INFO level
    config.parseFile("#dark.xml")
    config.parseFile("#preview.xml")
    //config.setLimitFPS(true)
    //config.setCreateMovie(true)
}

void runSketch() {
    Shape sh = Shape.square()
    LaTeXMathObject t = LaTeXMathObject.make('$e^{i\\pi}+1=0,\\ (x+y)=3$')
    t.setLatexStyle("colorful")
    add(t)
    play.rotate(3, 2 * PI, sh)
    waitSeconds(1)

}