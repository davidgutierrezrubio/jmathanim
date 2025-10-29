config.setLoggingLevel(LogLevel.DEBUG);//INFO level
config.parseFile("#light.xml")
config.parseFile("#preview.xml")
//config.setLimitFPS(true)
//config.setCreateMovie(true)


Shape sh= Shape.square()
play.rotate(3,2*PI,sh)
LatexMathObject t=LatexMathObject.make('$a^2+b^2=c^2$');
//t.setLaTexStyle("colorblind");
scene.add(t);
config.setMediaHeight(200)
//config.parseFile("#dark.xml")
scene.waitSeconds(1)

//BoxLayout bl=BoxLayout.make(3);
//scene.playAnimation(Commands.setLayout(1,bl,mg))
//scene.playAnimation(ag)

