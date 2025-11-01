package com.jmathanim.Utils;

import com.jmathanim.Styling.JMColor;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorParser {
    public static final HashMap<String, Integer[]> COLOR_NAMES = new HashMap<String, Integer[]>() {{
        put("aliceblue", new Integer[]{240, 248, 255});
        put("antiquewhite", new Integer[]{250, 235, 215});
        put("antiquewhite1", new Integer[]{255, 239, 219});
        put("antiquewhite2", new Integer[]{238, 223, 204});
        put("antiquewhite3", new Integer[]{205, 192, 176});
        put("antiquewhite4", new Integer[]{139, 131, 120});
        put("aqua", new Integer[]{0, 255, 255});
        put("aquamarine", new Integer[]{127, 255, 212});
        put("aquamarine1", new Integer[]{127, 255, 212});
        put("aquamarine2", new Integer[]{118, 238, 198});
        put("aquamarine3", new Integer[]{102, 205, 170});
        put("aquamarine4", new Integer[]{69, 139, 116});
        put("azure", new Integer[]{240, 255, 255});
        put("azure1", new Integer[]{240, 255, 255});
        put("azure2", new Integer[]{224, 238, 238});
        put("azure3", new Integer[]{193, 205, 205});
        put("azure4", new Integer[]{131, 139, 139});
        put("beige", new Integer[]{245, 245, 220});
        put("bisque", new Integer[]{255, 228, 196});
        put("bisque1", new Integer[]{255, 228, 196});
        put("bisque2", new Integer[]{238, 213, 183});
        put("bisque3", new Integer[]{205, 183, 158});
        put("bisque4", new Integer[]{139, 125, 107});
        put("black", new Integer[]{0, 0, 0});
        put("blanchedalmond", new Integer[]{255, 235, 205});
        put("blue", new Integer[]{0, 0, 255});
        put("blue1", new Integer[]{0, 0, 255});
        put("blue2", new Integer[]{0, 0, 238});
        put("blue3", new Integer[]{0, 0, 205});
        put("blue4", new Integer[]{0, 0, 139});
        put("blueviolet", new Integer[]{138, 43, 226});
        put("brown", new Integer[]{165, 42, 42});
        put("brown1", new Integer[]{255, 64, 64});
        put("brown2", new Integer[]{238, 59, 59});
        put("brown3", new Integer[]{205, 51, 51});
        put("brown4", new Integer[]{139, 35, 35});
        put("burlywood", new Integer[]{222, 184, 135});
        put("burlywood1", new Integer[]{255, 211, 155});
        put("burlywood2", new Integer[]{238, 197, 143});
        put("burlywood3", new Integer[]{205, 170, 124});
        put("burlywood4", new Integer[]{139, 115, 85});
        put("cadetblue", new Integer[]{95, 158, 160});
        put("cadetblue1", new Integer[]{152, 245, 255});
        put("cadetblue2", new Integer[]{142, 229, 238});
        put("cadetblue3", new Integer[]{122, 197, 205});
        put("cadetblue4", new Integer[]{83, 134, 139});
        put("chartreuse", new Integer[]{127, 255, 0});
        put("chartreuse1", new Integer[]{127, 255, 0});
        put("chartreuse2", new Integer[]{118, 238, 0});
        put("chartreuse3", new Integer[]{102, 205, 0});
        put("chartreuse4", new Integer[]{69, 139, 0});
        put("chocolate", new Integer[]{210, 105, 30});
        put("chocolate1", new Integer[]{255, 128, 36});
        put("chocolate2", new Integer[]{238, 118, 33});
        put("chocolate3", new Integer[]{205, 102, 29});
        put("chocolate4", new Integer[]{139, 69, 19});
        put("coral", new Integer[]{255, 127, 80});
        put("coral1", new Integer[]{255, 114, 86});
        put("coral2", new Integer[]{238, 106, 80});
        put("coral3", new Integer[]{205, 91, 69});
        put("coral4", new Integer[]{139, 62, 47});
        put("cornflowerblue", new Integer[]{100, 149, 237});
        put("cornsilk", new Integer[]{255, 248, 220});
        put("cornsilk1", new Integer[]{255, 248, 220});
        put("cornsilk2", new Integer[]{238, 232, 205});
        put("cornsilk3", new Integer[]{205, 200, 177});
        put("cornsilk4", new Integer[]{139, 136, 120});
        put("crimson", new Integer[]{220, 20, 60});
        put("cyan", new Integer[]{0, 255, 255});
        put("cyan1", new Integer[]{0, 255, 255});
        put("cyan2", new Integer[]{0, 238, 238});
        put("cyan3", new Integer[]{0, 205, 205});
        put("cyan4", new Integer[]{0, 139, 139});
        put("darkblue", new Integer[]{0, 0, 139});
        put("darkcyan", new Integer[]{0, 139, 139});
        put("darkgoldenrod", new Integer[]{184, 134, 11});
        put("darkgoldenrod1", new Integer[]{255, 185, 15});
        put("darkgoldenrod2", new Integer[]{238, 173, 14});
        put("darkgoldenrod3", new Integer[]{205, 149, 12});
        put("darkgoldenrod4", new Integer[]{139, 101, 8});
        put("darkgray", new Integer[]{169, 169, 169});
        put("darkgreen", new Integer[]{0, 100, 0});
        put("darkkhaki", new Integer[]{189, 183, 107});
        put("darkmagenta", new Integer[]{139, 0, 139});
        put("darkolivegreen", new Integer[]{85, 107, 47});
        put("darkolivegreen1", new Integer[]{202, 255, 112});
        put("darkolivegreen2", new Integer[]{188, 238, 104});
        put("darkolivegreen3", new Integer[]{162, 205, 90});
        put("darkolivegreen4", new Integer[]{110, 139, 61});
        put("darkorange", new Integer[]{255, 140, 0});
        put("darkorange1", new Integer[]{255, 127, 0});
        put("darkorange2", new Integer[]{238, 118, 0});
        put("darkorange3", new Integer[]{205, 102, 0});
        put("darkorange4", new Integer[]{139, 69, 0});
        put("darkorchid", new Integer[]{153, 50, 204});
        put("darkorchid1", new Integer[]{191, 62, 255});
        put("darkorchid2", new Integer[]{178, 58, 238});
        put("darkorchid3", new Integer[]{154, 50, 205});
        put("darkorchid4", new Integer[]{104, 34, 139});
        put("darkred", new Integer[]{139, 0, 0});
        put("darksalmon", new Integer[]{233, 150, 122});
        put("darkseagreen", new Integer[]{143, 188, 143});
        put("darkseagreen1", new Integer[]{193, 255, 193});
        put("darkseagreen2", new Integer[]{180, 238, 180});
        put("darkseagreen3", new Integer[]{155, 205, 155});
        put("darkseagreen4", new Integer[]{105, 139, 105});
        put("darkslateblue", new Integer[]{72, 61, 139});
        put("darkslategray", new Integer[]{47, 79, 79});
        put("darkslategray1", new Integer[]{151, 255, 255});
        put("darkslategray2", new Integer[]{141, 238, 238});
        put("darkslategray3", new Integer[]{121, 205, 205});
        put("darkslategray4", new Integer[]{82, 139, 139});
        put("darkturquoise", new Integer[]{0, 206, 209});
        put("darkviolet", new Integer[]{148, 0, 211});
        put("deeppink", new Integer[]{255, 20, 147});
        put("deeppink1", new Integer[]{255, 20, 147});
        put("deeppink2", new Integer[]{238, 18, 137});
        put("deeppink3", new Integer[]{205, 16, 118});
        put("deeppink4", new Integer[]{139, 10, 80});
        put("deepskyblue", new Integer[]{0, 191, 255});
        put("deepskyblue1", new Integer[]{0, 191, 255});
        put("deepskyblue2", new Integer[]{0, 178, 238});
        put("deepskyblue3", new Integer[]{0, 154, 205});
        put("deepskyblue4", new Integer[]{0, 105, 139});
        put("dimgray", new Integer[]{105, 105, 105});
        put("dodgerblue", new Integer[]{30, 144, 255});
        put("dodgerblue1", new Integer[]{30, 144, 255});
        put("dodgerblue2", new Integer[]{28, 134, 238});
        put("dodgerblue3", new Integer[]{24, 116, 205});
        put("dodgerblue4", new Integer[]{16, 78, 139});
        put("firebrick", new Integer[]{178, 34, 34});
        put("firebrick1", new Integer[]{255, 48, 48});
        put("firebrick2", new Integer[]{238, 44, 44});
        put("firebrick3", new Integer[]{205, 38, 38});
        put("firebrick4", new Integer[]{139, 26, 26});
        put("floralwhite", new Integer[]{255, 250, 240});
        put("forestgreen", new Integer[]{34, 139, 34});
        put("fuchsia", new Integer[]{255, 0, 255});
        put("gainsboro", new Integer[]{220, 220, 220});
        put("ghostwhite", new Integer[]{248, 248, 255});
        put("gold", new Integer[]{255, 215, 0});
        put("gold1", new Integer[]{255, 215, 0});
        put("gold2", new Integer[]{238, 201, 0});
        put("gold3", new Integer[]{205, 173, 0});
        put("gold4", new Integer[]{139, 117, 0});
        put("goldenrod", new Integer[]{218, 165, 32});
        put("goldenrod1", new Integer[]{255, 193, 37});
        put("goldenrod2", new Integer[]{238, 180, 34});
        put("goldenrod3", new Integer[]{205, 155, 29});
        put("goldenrod4", new Integer[]{139, 105, 20});
        put("gray", new Integer[]{128, 128, 128});
        put("gray1", new Integer[]{3, 3, 3});
        put("gray2", new Integer[]{5, 5, 5});
        put("gray3", new Integer[]{8, 8, 8});
        put("gray4", new Integer[]{10, 10, 10});
        put("gray5", new Integer[]{13, 13, 13});
        put("gray6", new Integer[]{15, 15, 15});
        put("gray7", new Integer[]{18, 18, 18});
        put("gray8", new Integer[]{20, 20, 20});
        put("gray9", new Integer[]{23, 23, 23});
        put("gray10", new Integer[]{26, 26, 26});
        put("gray11", new Integer[]{28, 28, 28});
        put("gray12", new Integer[]{31, 31, 31});
        put("gray13", new Integer[]{33, 33, 33});
        put("gray14", new Integer[]{36, 36, 36});
        put("gray15", new Integer[]{38, 38, 38});
        put("gray16", new Integer[]{41, 41, 41});
        put("gray17", new Integer[]{43, 43, 43});
        put("gray18", new Integer[]{46, 46, 46});
        put("gray19", new Integer[]{48, 48, 48});
        put("gray20", new Integer[]{51, 51, 51});
        put("gray21", new Integer[]{54, 54, 54});
        put("gray22", new Integer[]{56, 56, 56});
        put("gray23", new Integer[]{59, 59, 59});
        put("gray24", new Integer[]{61, 61, 61});
        put("gray25", new Integer[]{64, 64, 64});
        put("gray26", new Integer[]{66, 66, 66});
        put("gray27", new Integer[]{69, 69, 69});
        put("gray28", new Integer[]{71, 71, 71});
        put("gray29", new Integer[]{74, 74, 74});
        put("gray30", new Integer[]{77, 77, 77});
        put("gray31", new Integer[]{79, 79, 79});
        put("gray32", new Integer[]{82, 82, 82});
        put("gray33", new Integer[]{84, 84, 84});
        put("gray34", new Integer[]{87, 87, 87});
        put("gray35", new Integer[]{89, 89, 89});
        put("gray36", new Integer[]{92, 92, 92});
        put("gray37", new Integer[]{94, 94, 94});
        put("gray38", new Integer[]{97, 97, 97});
        put("gray39", new Integer[]{99, 99, 99});
        put("gray40", new Integer[]{102, 102, 102});
        put("gray41", new Integer[]{105, 105, 105});
        put("gray42", new Integer[]{107, 107, 107});
        put("gray43", new Integer[]{110, 110, 110});
        put("gray44", new Integer[]{112, 112, 112});
        put("gray45", new Integer[]{115, 115, 115});
        put("gray46", new Integer[]{117, 117, 117});
        put("gray47", new Integer[]{120, 120, 120});
        put("gray48", new Integer[]{122, 122, 122});
        put("gray49", new Integer[]{125, 125, 125});
        put("gray50", new Integer[]{127, 127, 127});
        put("gray51", new Integer[]{130, 130, 130});
        put("gray52", new Integer[]{133, 133, 133});
        put("gray53", new Integer[]{135, 135, 135});
        put("gray54", new Integer[]{138, 138, 138});
        put("gray55", new Integer[]{140, 140, 140});
        put("gray56", new Integer[]{143, 143, 143});
        put("gray57", new Integer[]{145, 145, 145});
        put("gray58", new Integer[]{148, 148, 148});
        put("gray59", new Integer[]{150, 150, 150});
        put("gray60", new Integer[]{153, 153, 153});
        put("gray61", new Integer[]{156, 156, 156});
        put("gray62", new Integer[]{158, 158, 158});
        put("gray63", new Integer[]{161, 161, 161});
        put("gray64", new Integer[]{163, 163, 163});
        put("gray65", new Integer[]{166, 166, 166});
        put("gray66", new Integer[]{168, 168, 168});
        put("gray67", new Integer[]{171, 171, 171});
        put("gray68", new Integer[]{173, 173, 173});
        put("gray69", new Integer[]{176, 176, 176});
        put("gray70", new Integer[]{179, 179, 179});
        put("gray71", new Integer[]{181, 181, 181});
        put("gray72", new Integer[]{184, 184, 184});
        put("gray73", new Integer[]{186, 186, 186});
        put("gray74", new Integer[]{189, 189, 189});
        put("gray75", new Integer[]{191, 191, 191});
        put("gray76", new Integer[]{194, 194, 194});
        put("gray77", new Integer[]{196, 196, 196});
        put("gray78", new Integer[]{199, 199, 199});
        put("gray79", new Integer[]{201, 201, 201});
        put("gray80", new Integer[]{204, 204, 204});
        put("gray81", new Integer[]{207, 207, 207});
        put("gray82", new Integer[]{209, 209, 209});
        put("gray83", new Integer[]{212, 212, 212});
        put("gray84", new Integer[]{214, 214, 214});
        put("gray85", new Integer[]{217, 217, 217});
        put("gray86", new Integer[]{219, 219, 219});
        put("gray87", new Integer[]{222, 222, 222});
        put("gray88", new Integer[]{224, 224, 224});
        put("gray89", new Integer[]{227, 227, 227});
        put("gray90", new Integer[]{229, 229, 229});
        put("gray91", new Integer[]{232, 232, 232});
        put("gray92", new Integer[]{235, 235, 235});
        put("gray93", new Integer[]{237, 237, 237});
        put("gray94", new Integer[]{240, 240, 240});
        put("gray95", new Integer[]{242, 242, 242});
        put("gray96", new Integer[]{245, 245, 245});
        put("gray97", new Integer[]{247, 247, 247});
        put("gray98", new Integer[]{250, 250, 250});
        put("gray99", new Integer[]{252, 252, 252});
        put("green", new Integer[]{0, 128, 0});
        put("green1", new Integer[]{0, 255, 0});
        put("green2", new Integer[]{0, 238, 0});
        put("green3", new Integer[]{0, 205, 0});
        put("green4", new Integer[]{0, 139, 0});
        put("greenyellow", new Integer[]{173, 255, 47});
        put("honeydew", new Integer[]{240, 255, 240});
        put("honeydew1", new Integer[]{240, 255, 240});
        put("honeydew2", new Integer[]{224, 238, 224});
        put("honeydew3", new Integer[]{193, 205, 193});
        put("honeydew4", new Integer[]{131, 139, 131});
        put("hotpink", new Integer[]{255, 105, 180});
        put("hotpink1", new Integer[]{255, 110, 180});
        put("hotpink2", new Integer[]{238, 106, 167});
        put("hotpink3", new Integer[]{205, 96, 144});
        put("hotpink4", new Integer[]{139, 58, 98});
        put("indianred", new Integer[]{205, 92, 92});
        put("indianred1", new Integer[]{255, 106, 106});
        put("indianred2", new Integer[]{238, 99, 99});
        put("indianred3", new Integer[]{205, 85, 85});
        put("indianred4", new Integer[]{139, 58, 58});
        put("indigo", new Integer[]{75, 0, 130});
        put("ivory", new Integer[]{255, 255, 240});
        put("ivory1", new Integer[]{255, 255, 240});
        put("ivory2", new Integer[]{238, 238, 224});
        put("ivory3", new Integer[]{205, 205, 193});
        put("ivory4", new Integer[]{139, 139, 131});
        put("khaki", new Integer[]{240, 230, 140});
        put("khaki1", new Integer[]{255, 246, 143});
        put("khaki2", new Integer[]{238, 230, 133});
        put("khaki3", new Integer[]{205, 198, 115});
        put("khaki4", new Integer[]{139, 134, 78});
        put("lavender", new Integer[]{230, 230, 250});
        put("lavenderblush", new Integer[]{255, 240, 245});
        put("lavenderblush1", new Integer[]{255, 240, 245});
        put("lavenderblush2", new Integer[]{238, 224, 229});
        put("lavenderblush3", new Integer[]{205, 193, 197});
        put("lavenderblush4", new Integer[]{139, 131, 134});
        put("lawngreen", new Integer[]{124, 252, 0});
        put("lemonchiffon", new Integer[]{255, 250, 205});
        put("lemonchiffon1", new Integer[]{255, 250, 205});
        put("lemonchiffon2", new Integer[]{238, 233, 191});
        put("lemonchiffon3", new Integer[]{205, 201, 165});
        put("lemonchiffon4", new Integer[]{139, 137, 112});
        put("lightblue", new Integer[]{173, 216, 230});
        put("lightblue1", new Integer[]{191, 239, 255});
        put("lightblue2", new Integer[]{178, 223, 238});
        put("lightblue3", new Integer[]{154, 192, 205});
        put("lightblue4", new Integer[]{104, 131, 139});
        put("lightcoral", new Integer[]{240, 128, 128});
        put("lightcyan", new Integer[]{224, 255, 255});
        put("lightcyan1", new Integer[]{224, 255, 255});
        put("lightcyan2", new Integer[]{209, 238, 238});
        put("lightcyan3", new Integer[]{180, 205, 205});
        put("lightcyan4", new Integer[]{122, 139, 139});
        put("lightgoldenrod", new Integer[]{238, 221, 130});
        put("lightgoldenrod1", new Integer[]{255, 236, 139});
        put("lightgoldenrod2", new Integer[]{238, 220, 130});
        put("lightgoldenrod3", new Integer[]{205, 190, 112});
        put("lightgoldenrod4", new Integer[]{139, 129, 76});
        put("lightgoldenrodyellow", new Integer[]{250, 250, 210});
        put("lightgray", new Integer[]{211, 211, 211});
        put("lightgreen", new Integer[]{144, 238, 144});
        put("lightpink", new Integer[]{255, 182, 193});
        put("lightpink1", new Integer[]{255, 174, 185});
        put("lightpink2", new Integer[]{238, 162, 173});
        put("lightpink3", new Integer[]{205, 140, 149});
        put("lightpink4", new Integer[]{139, 95, 101});
        put("lightsalmon", new Integer[]{255, 160, 122});
        put("lightsalmon1", new Integer[]{255, 160, 122});
        put("lightsalmon2", new Integer[]{238, 149, 112});
        put("lightsalmon3", new Integer[]{205, 129, 96});
        put("lightsalmon4", new Integer[]{139, 87, 66});
        put("lightseagreen", new Integer[]{32, 178, 170});
        put("lightskyblue", new Integer[]{135, 206, 250});
        put("lightskyblue1", new Integer[]{176, 226, 255});
        put("lightskyblue2", new Integer[]{164, 211, 238});
        put("lightskyblue3", new Integer[]{141, 182, 205});
        put("lightskyblue4", new Integer[]{96, 123, 139});
        put("lightslateblue", new Integer[]{132, 112, 255});
        put("lightslategray", new Integer[]{119, 136, 153});
        put("lightsteelblue", new Integer[]{176, 196, 222});
        put("lightsteelblue1", new Integer[]{202, 225, 255});
        put("lightsteelblue2", new Integer[]{188, 210, 238});
        put("lightsteelblue3", new Integer[]{162, 181, 205});
        put("lightsteelblue4", new Integer[]{110, 123, 139});
        put("lightyellow", new Integer[]{255, 255, 224});
        put("lightyellow1", new Integer[]{255, 255, 224});
        put("lightyellow2", new Integer[]{238, 238, 209});
        put("lightyellow3", new Integer[]{205, 205, 180});
        put("lightyellow4", new Integer[]{139, 139, 122});
        put("lime", new Integer[]{0, 255, 0});
        put("limegreen", new Integer[]{50, 205, 50});
        put("linen", new Integer[]{250, 240, 230});
        put("magenta", new Integer[]{255, 0, 255});
        put("magenta1", new Integer[]{255, 0, 255});
        put("magenta2", new Integer[]{238, 0, 238});
        put("magenta3", new Integer[]{205, 0, 205});
        put("magenta4", new Integer[]{139, 0, 139});
        put("maroon", new Integer[]{128, 0, 0});
        put("maroon1", new Integer[]{255, 52, 179});
        put("maroon2", new Integer[]{238, 48, 167});
        put("maroon3", new Integer[]{205, 41, 144});
        put("maroon4", new Integer[]{139, 28, 98});
        put("mediumaquamarine", new Integer[]{102, 205, 170});
        put("mediumblue", new Integer[]{0, 0, 205});
        put("mediumorchid", new Integer[]{186, 85, 211});
        put("mediumorchid1", new Integer[]{224, 102, 255});
        put("mediumorchid2", new Integer[]{209, 95, 238});
        put("mediumorchid3", new Integer[]{180, 82, 205});
        put("mediumorchid4", new Integer[]{122, 55, 139});
        put("mediumpurple", new Integer[]{147, 112, 219});
        put("mediumpurple1", new Integer[]{171, 130, 255});
        put("mediumpurple2", new Integer[]{159, 121, 238});
        put("mediumpurple3", new Integer[]{137, 104, 205});
        put("mediumpurple4", new Integer[]{93, 71, 139});
        put("mediumseagreen", new Integer[]{60, 179, 113});
        put("mediumslateblue", new Integer[]{123, 104, 238});
        put("mediumspringgreen", new Integer[]{0, 250, 154});
        put("mediumturquoise", new Integer[]{72, 209, 204});
        put("mediumvioletred", new Integer[]{199, 21, 133});
        put("midnightblue", new Integer[]{25, 25, 112});
        put("mintcream", new Integer[]{245, 255, 250});
        put("mistyrose", new Integer[]{255, 228, 225});
        put("mistyrose1", new Integer[]{255, 228, 225});
        put("mistyrose2", new Integer[]{238, 213, 210});
        put("mistyrose3", new Integer[]{205, 183, 181});
        put("mistyrose4", new Integer[]{139, 125, 123});
        put("moccasin", new Integer[]{255, 228, 181});
        put("navajowhite", new Integer[]{255, 222, 173});
        put("navajowhite1", new Integer[]{255, 222, 173});
        put("navajowhite2", new Integer[]{238, 207, 161});
        put("navajowhite3", new Integer[]{205, 179, 139});
        put("navajowhite4", new Integer[]{139, 121, 94});
        put("navy", new Integer[]{0, 0, 128});
        put("navyblue", new Integer[]{0, 0, 128});
        put("oldlace", new Integer[]{253, 245, 230});
        put("olive", new Integer[]{128, 128, 0});
        put("olivedrab", new Integer[]{107, 142, 35});
        put("olivedrab1", new Integer[]{192, 255, 62});
        put("olivedrab2", new Integer[]{179, 238, 58});
        put("olivedrab3", new Integer[]{154, 205, 50});
        put("olivedrab4", new Integer[]{105, 139, 34});
        put("orange", new Integer[]{255, 165, 0});
        put("orange1", new Integer[]{255, 165, 0});
        put("orange2", new Integer[]{238, 154, 0});
        put("orange3", new Integer[]{205, 133, 0});
        put("orange4", new Integer[]{139, 90, 0});
        put("orangered", new Integer[]{255, 69, 0});
        put("orangered1", new Integer[]{255, 69, 0});
        put("orangered2", new Integer[]{238, 64, 0});
        put("orangered3", new Integer[]{205, 55, 0});
        put("orangered4", new Integer[]{139, 37, 0});
        put("orchid", new Integer[]{218, 112, 214});
        put("orchid1", new Integer[]{255, 131, 250});
        put("orchid2", new Integer[]{238, 122, 233});
        put("orchid3", new Integer[]{205, 105, 201});
        put("orchid4", new Integer[]{139, 71, 137});
        put("palegoldenrod", new Integer[]{238, 232, 170});
        put("palegreen", new Integer[]{152, 251, 152});
        put("palegreen1", new Integer[]{154, 255, 154});
        put("palegreen2", new Integer[]{144, 238, 144});
        put("palegreen3", new Integer[]{124, 205, 124});
        put("palegreen4", new Integer[]{84, 139, 84});
        put("paleturquoise", new Integer[]{175, 238, 238});
        put("paleturquoise1", new Integer[]{187, 255, 255});
        put("paleturquoise2", new Integer[]{174, 238, 238});
        put("paleturquoise3", new Integer[]{150, 205, 205});
        put("paleturquoise4", new Integer[]{102, 139, 139});
        put("palevioletred", new Integer[]{219, 112, 147});
        put("palevioletred1", new Integer[]{255, 130, 171});
        put("palevioletred2", new Integer[]{238, 121, 159});
        put("palevioletred3", new Integer[]{205, 104, 137});
        put("palevioletred4", new Integer[]{139, 71, 93});
        put("papayawhip", new Integer[]{255, 239, 213});
        put("peachpuff", new Integer[]{255, 218, 185});
        put("peachpuff1", new Integer[]{255, 218, 185});
        put("peachpuff2", new Integer[]{238, 203, 173});
        put("peachpuff3", new Integer[]{205, 175, 149});
        put("peachpuff4", new Integer[]{139, 119, 101});
        put("peru", new Integer[]{205, 133, 63});
        put("pink", new Integer[]{255, 192, 203});
        put("pink1", new Integer[]{255, 181, 197});
        put("pink2", new Integer[]{238, 169, 184});
        put("pink3", new Integer[]{205, 145, 158});
        put("pink4", new Integer[]{139, 99, 108});
        put("plum", new Integer[]{221, 160, 221});
        put("plum1", new Integer[]{255, 187, 255});
        put("plum2", new Integer[]{238, 174, 238});
        put("plum3", new Integer[]{205, 150, 205});
        put("plum4", new Integer[]{139, 102, 139});
        put("powderblue", new Integer[]{176, 224, 230});
        put("purple", new Integer[]{128, 0, 128});
        put("purple1", new Integer[]{155, 48, 255});
        put("purple2", new Integer[]{145, 44, 238});
        put("purple3", new Integer[]{125, 38, 205});
        put("purple4", new Integer[]{85, 26, 139});
        put("red", new Integer[]{255, 0, 0});
        put("red1", new Integer[]{255, 0, 0});
        put("red2", new Integer[]{238, 0, 0});
        put("red3", new Integer[]{205, 0, 0});
        put("red4", new Integer[]{139, 0, 0});
        put("rosybrown", new Integer[]{188, 143, 143});
        put("rosybrown1", new Integer[]{255, 193, 193});
        put("rosybrown2", new Integer[]{238, 180, 180});
        put("rosybrown3", new Integer[]{205, 155, 155});
        put("rosybrown4", new Integer[]{139, 105, 105});
        put("royalblue", new Integer[]{65, 105, 225});
        put("royalblue1", new Integer[]{72, 118, 255});
        put("royalblue2", new Integer[]{67, 110, 238});
        put("royalblue3", new Integer[]{58, 95, 205});
        put("royalblue4", new Integer[]{39, 64, 139});
        put("saddlebrown", new Integer[]{139, 69, 19});
        put("salmon", new Integer[]{250, 128, 114});
        put("salmon1", new Integer[]{255, 140, 105});
        put("salmon2", new Integer[]{238, 130, 98});
        put("salmon3", new Integer[]{205, 112, 84});
        put("salmon4", new Integer[]{139, 76, 57});
        put("sandybrown", new Integer[]{244, 164, 96});
        put("seagreen", new Integer[]{46, 139, 87});
        put("seagreen1", new Integer[]{84, 255, 159});
        put("seagreen2", new Integer[]{78, 238, 148});
        put("seagreen3", new Integer[]{67, 205, 128});
        put("seagreen4", new Integer[]{46, 139, 87});
        put("seashell", new Integer[]{255, 245, 238});
        put("seashell1", new Integer[]{255, 245, 238});
        put("seashell2", new Integer[]{238, 229, 233});
        put("seashell3", new Integer[]{205, 197, 201});
        put("seashell4", new Integer[]{139, 134, 137});
        put("sienna", new Integer[]{160, 82, 45});
        put("sienna1", new Integer[]{255, 130, 71});
        put("sienna2", new Integer[]{238, 121, 66});
        put("sienna3", new Integer[]{205, 104, 57});
        put("sienna4", new Integer[]{139, 71, 38});
        put("silver", new Integer[]{192, 192, 192});
        put("skyblue", new Integer[]{135, 206, 235});
        put("skyblue1", new Integer[]{135, 206, 255});
        put("skyblue2", new Integer[]{126, 192, 238});
        put("skyblue3", new Integer[]{108, 166, 205});
        put("skyblue4", new Integer[]{74, 112, 139});
        put("slateblue", new Integer[]{106, 90, 205});
        put("slateblue1", new Integer[]{131, 111, 255});
        put("slateblue2", new Integer[]{122, 103, 238});
        put("slateblue3", new Integer[]{105, 89, 205});
        put("slateblue4", new Integer[]{71, 60, 139});
        put("slategray", new Integer[]{112, 128, 144});
        put("slategray1", new Integer[]{198, 226, 255});
        put("slategray2", new Integer[]{185, 211, 238});
        put("slategray3", new Integer[]{159, 182, 205});
        put("slategray4", new Integer[]{108, 123, 139});
        put("snow", new Integer[]{255, 250, 250});
        put("snow1", new Integer[]{255, 250, 250});
        put("snow2", new Integer[]{238, 233, 233});
        put("snow3", new Integer[]{205, 201, 201});
        put("snow4", new Integer[]{139, 137, 137});
        put("springgreen", new Integer[]{0, 255, 127});
        put("springgreen1", new Integer[]{0, 255, 127});
        put("springgreen2", new Integer[]{0, 238, 118});
        put("springgreen3", new Integer[]{0, 205, 102});
        put("springgreen4", new Integer[]{0, 139, 69});
        put("steelblue", new Integer[]{70, 130, 180});
        put("steelblue1", new Integer[]{99, 184, 255});
        put("steelblue2", new Integer[]{92, 172, 238});
        put("steelblue3", new Integer[]{79, 148, 205});
        put("steelblue4", new Integer[]{54, 100, 139});
        put("tan", new Integer[]{210, 180, 140});
        put("tan1", new Integer[]{255, 165, 79});
        put("tan2", new Integer[]{238, 154, 73});
        put("tan3", new Integer[]{205, 133, 63});
        put("tan4", new Integer[]{139, 90, 43});
        put("teal", new Integer[]{0, 128, 128});
        put("thistle", new Integer[]{216, 191, 216});
        put("thistle1", new Integer[]{255, 225, 255});
        put("thistle2", new Integer[]{238, 210, 238});
        put("thistle3", new Integer[]{205, 181, 205});
        put("thistle4", new Integer[]{139, 123, 139});
        put("tomato", new Integer[]{255, 99, 71});
        put("tomato1", new Integer[]{255, 99, 71});
        put("tomato2", new Integer[]{238, 92, 66});
        put("tomato3", new Integer[]{205, 79, 57});
        put("tomato4", new Integer[]{139, 54, 38});
        put("turquoise", new Integer[]{64, 224, 208});
        put("turquoise1", new Integer[]{0, 245, 255});
        put("turquoise2", new Integer[]{0, 229, 238});
        put("turquoise3", new Integer[]{0, 197, 205});
        put("turquoise4", new Integer[]{0, 134, 139});
        put("violet", new Integer[]{238, 130, 238});
        put("violetred", new Integer[]{208, 32, 144});
        put("violetred1", new Integer[]{255, 62, 150});
        put("violetred2", new Integer[]{238, 58, 140});
        put("violetred3", new Integer[]{205, 50, 120});
        put("violetred4", new Integer[]{139, 34, 82});
        put("wheat", new Integer[]{245, 222, 179});
        put("wheat1", new Integer[]{255, 231, 186});
        put("wheat2", new Integer[]{238, 216, 174});
        put("wheat3", new Integer[]{205, 186, 150});
        put("wheat4", new Integer[]{139, 126, 102});
        put("white", new Integer[]{255, 255, 255});
        put("whitesmoke", new Integer[]{245, 245, 245});
        put("yellow", new Integer[]{255, 255, 0});
        put("yellow1", new Integer[]{255, 255, 0});
        put("yellow2", new Integer[]{238, 238, 0});
        put("yellow3", new Integer[]{205, 205, 0});
        put("yellow4", new Integer[]{139, 139, 0});
        put("yellowgreen", new Integer[]{154, 205, 50});
    }};

    /**
     * Parses a hex string defining a color, starting with a # in format #RGB, #RGBA, #RRGGBB, #RRGGBBAA
     * @param hexColorString The hex string
     * @return The JMColor created from the hex string
     */
    public static JMColor parseHexColor(String hexColorString) {
        String hex = hexColorString.substring(1).toUpperCase();
        int len = hex.length();

        String rHex, gHex, bHex, aHex;

        switch (len) {
            case 3: // #RGB -> #RRGGBBFF
                rHex = hex.substring(0, 1) + hex.substring(0, 1);
                gHex = hex.substring(1, 2) + hex.substring(1, 2);
                bHex = hex.substring(2, 3) + hex.substring(2, 3);
                aHex = "FF"; // Opacidad por defecto (Opaco)
                break;

            case 4: // #RGBA -> #RRGGBBAA
                rHex = hex.substring(0, 1) + hex.substring(0, 1);
                gHex = hex.substring(1, 2) + hex.substring(1, 2);
                bHex = hex.substring(2, 3) + hex.substring(2, 3);
                aHex = hex.substring(3, 4) + hex.substring(3, 4);
                break;

            case 6: // #RRGGBB -> #RRGGBBFF
                rHex = hex.substring(0, 2);
                gHex = hex.substring(2, 4);
                bHex = hex.substring(4, 6);
                aHex = "FF"; // Opacidad por defecto (Opaco)
                break;

            case 8: // #RRGGBBAA
                rHex = hex.substring(0, 2);
                gHex = hex.substring(2, 4);
                bHex = hex.substring(4, 6);
                aHex = hex.substring(6, 8);
                break;

            default:
                JMathAnimScene.logger.warn("Color format not supported: " + LogUtils.method(hex) + ". Using WHITE color instead");
                return JMColor.WHITE;
        }

        // 4. Convertir los componentes hexadecimales a enteros decimales (0-255)
        try {
            int r = Integer.parseInt(rHex, 16);
            int g = Integer.parseInt(gHex, 16);
            int b = Integer.parseInt(bHex, 16);
            int a = Integer.parseInt(aHex, 16);

            return JMColor.rgbaInt(r, g, b, a);
        } catch (NumberFormatException e) {
            JMathAnimScene.logger.warn("Color format not supported: " + LogUtils.method(hex) + ". Using WHITE color instead");
            return JMColor.WHITE;
        }
    }

    /**
     * Parses string with the format rgb(r,g,b) or rgba(r,g,b,a). Parameters can be integers from 0 to 255 or percentage values
     * @param colorString The color format string
     * @return The JMColor created from the string
     */
    public static JMColor parseRGBorRGBA(String colorString) {
        if (colorString == null) {
          JMathAnimScene.logger.warn("Null color string, returning WHITE color instead");
          return JMColor.WHITE;
        }

        String cleanedString = colorString.replaceAll("\\s", "").trim();

        Pattern pattern = Pattern.compile("^(rgb|rgba)\\(([\\d\\.]+%?),([\\d\\.]+%?),([\\d\\.]+%?)(?:,([\\d\\.]+))?\\)$");
        Matcher matcher = pattern.matcher(cleanedString);

        if (matcher.find()) {
            double[] rgbaValues = new double[4];
            for (int i = 0; i < 3; i++) {
                rgbaValues[i] = parseIntegerOrPercentage(matcher.group(i + 2));
            }
            String alphaString = matcher.group(5);
            if (alphaString != null) {
                rgbaValues[3] = Double.parseDouble(alphaString);
            } else {
                rgbaValues[3] = 255.0;
            }

            return JMColor.rgbaInt((int) rgbaValues[0], (int) rgbaValues[1], (int) rgbaValues[2], (int) rgbaValues[3]);

        } else {
            return null;
        }
    }
    private static double parseIntegerOrPercentage(String componentString) {
        if (componentString.endsWith("%")) {
            // Es un porcentaje (ej. "50%")
            String percentageString = componentString.substring(0, componentString.length() - 1);
            double percentage = Double.parseDouble(percentageString);

            // Conversión de 0-100 a 0-255
            return (percentage / 100.0) * 255;
        } else {
            // Es un valor entero/double (ej. "255" o "127.5")
            return Double.parseDouble(componentString);
        }
    }
    /**
     * Parses string with the format hsl(h,s,l). Parameters can be integers from 0 to 255 or percentage values
     * @param colorString The color format string
     * @return The JMColor created from the string
     */
    public static JMColor parseHSL(String colorString) {
        if (colorString == null) {
            JMathAnimScene.logger.warn("Null color string, returning WHITE color instead");
            return JMColor.WHITE;
        }
        String cleanedString = colorString.replaceAll("\\s", "").trim();

        Pattern pattern = Pattern.compile("^(hsl)\\(([\\d\\.]+%?),([\\d\\.]+%?),([\\d\\.]+%?)\\)$");
        Matcher matcher = pattern.matcher(cleanedString);

        if (matcher.find()) {
            double[] hslValues = new double[3];
            for (int i = 0; i < 3; i++) {
                hslValues[i] = parseIntegerOrPercentage(matcher.group(i + 2));
            }
            int[] rgb = hslToRgb(hslValues[0], hslValues[1], hslValues[2]);
            return JMColor.rgbaInt(rgb[0], rgb[1], rgb[2], 255);

        } else {
            return null;
        }
    }

    private static int[] hslToRgb(double h, double s, double l) {
        // Asegurarse de que H esté en el rango [0, 360]
        h = h % 360.0;
        if (h < 0) h += 360.0;

        // Asegurarse de que S y L estén en el rango [0.0, 1.0]
        s = Math.min(1.0, Math.max(0.0, s));
        l = Math.min(1.0, Math.max(0.0, l));

        // Caso trivial: Si la saturación es 0, es gris.
        if (s == 0.0) {
            int gray = (int) Math.round(l * 255.0);
            return new int[]{gray, gray, gray};
        }

        // 1. Calcular Croma (C)
        double c = (1.0 - Math.abs(2.0 * l - 1.0)) * s;

        // 2. Calcular Punto Intermedio (X)
        double hPrime = h / 60.0;
        double x = c * (1.0 - Math.abs(hPrime % 2.0 - 1.0));

        // R', G', B' iniciales
        double r1 = 0.0;
        double g1 = 0.0;
        double b1 = 0.0;

        // 3. Determinar los valores primarios (R', G', B') basados en el sector H
        if (hPrime >= 0 && hPrime < 1) {
            r1 = c;
            g1 = x;
            b1 = 0;
        } else if (hPrime >= 1 && hPrime < 2) {
            r1 = x;
            g1 = c;
            b1 = 0;
        } else if (hPrime >= 2 && hPrime < 3) {
            r1 = 0;
            g1 = c;
            b1 = x;
        } else if (hPrime >= 3 && hPrime < 4) {
            r1 = 0;
            g1 = x;
            b1 = c;
        } else if (hPrime >= 4 && hPrime < 5) {
            r1 = x;
            g1 = 0;
            b1 = c;
        } else if (hPrime >= 5 && hPrime < 6) {
            r1 = c;
            g1 = 0;
            b1 = x;
        }

        // 4. Añadir componente de Luminosidad (m) y escalar a 0-255
        double m = l - c / 2.0;

        int r = (int) Math.round((r1 + m) * 255.0);
        int g = (int) Math.round((g1 + m) * 255.0);
        int b = (int) Math.round((b1 + m) * 255.0);

        return new int[]{r, g, b};
    }
}
