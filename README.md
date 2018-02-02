<h1 align="center"><b>JSisyphus</b></h1>

## What is JSisyphus?
*JSisyphus* is a command line utility program that creates tracks algorithmically for a [Sisyphus table](http://www.sisyphus-industries.com/), and also creates PNG files with emulations of those tracks' playback on the actual Sisyphus table.  Most of JSisyphus consists of classes and functions to make algorithmic creation of tracks for the Sisyphus table easier.  The algorithms themselves are defined by classes that the *user* of JSisyphus writes.  Some examples of such classes are included in the "examples" package: classes that use JSisyphus to create actual tracks that are playable on the Sisyphus table.

## Why does the world need JSisyphus?
Well, probably the world doesn't actually need *JSisyphus* &mdash; it's mainly here for the author's personal enjoyment, but with some faintish hope that someone else interested in creating Sisyphus table tracks will also find it useful.

## What, exactly, does *JSisyphus* do?
The model for *JSisyphus* is the standard Java graphics subsystem, specifically the *Graphics2D* class and all the other classes that work with it.  There are many deviations from that model to accommodate the polar coordinate system used by the Sisyphus table, as well as some peculiarities that arise from the fact that to the Sisyphus table, a "straight" line is actually an arithmetic (or Archimedian) spiral.  Fundamentally JSisyphus is a library, not a standalone program.  The *Main* class in the examples package is a good starting point to explore how you might use JSisyphus to create Sisyphus table tracks of your own.  When used as in the examples, *JSisyphus* has two outputs:
* .thr files that each contain a generated Sisyphus table track that can be uploaded directly to the Sisphus table for playback.
* .png files that each contain an *emulated* run of a track on the Sisyphus table.  These are generated much faster than the table can play them, and are therefore quite useful when developing an algorithmic track.

## Dependencies
The only dependency JSisyphus has is on Java 1.8 or higher.  There are no external libraries required.

## Getting started...
At least for now, JSisyphus is suitable *only* for a developer to use.  To make new tracks, you must be able to write (fairly simple) Java programs.  The easiest way for a developer to use JSisyphus is to clone this repository locally, then use a suitable IDE (the author uses "IDEA" by JetBrains, but any other modern IDE should do) to fool around with it.  The root Java package for JSisyphus is __com.slightlyloony.jsisyphus__.  Within that package you'll find the __examples__ package, which has several sample track generating classes, and a __Main__ class that runs them.  That Main class is something you can run yourself, and if you do you'll see all the example tracks generated on your system, with the .thr and .png files for each.

## Future directions...
If enough people are interested, the author might be persuaded to add a domain-specific language (DSL) as a front end for JSisyphus.  This would be a textual programming language that's specific to the problem of generating tracks for the Sisyphus table.  You would create a text file with the Sisyphus table "program", then run it through the DSL to produce the same outputs that JSisyphus does today.  Such a DSL would allow programmers who don't know Java, and even clever non-programmers, to create new tracks without having to program in Java at all.  If such a DSL would interest you, please let the author know!  

Here's a very simple example of what a Sisyphus table DSL might look like:

    erase all to center
    line to rho .3, theta 30 degrees
    arc around center 360 degrees

## Why is *JSisyphus*' code so awful?
The author is a retired software and hardware engineer who did this just for fun, and who (so far, anyway) has no code reviewers to upbraid him.  Please feel free to fill in this gap!  You may contact the author at tom@dilatush.com.

## How is *JSisyphus* licensed?
*JSisyphus* is licensed with the quite permissive MIT license:
> Created: January 10, 2018<br>
> Author: Tom Dilatush <tom@dilatush.com><br>
> Github:  https://github.com/SlightlyLoony/JSisyphus<br>
> License: MIT
> 
> Copyright 2017 Tom Dilatush (aka "SlightlyLoony")
> 
> Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so.
> 
> The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
> 
> THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE A AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Notes
### The Sisyphus table coordinate system as used in *JSisyphus*
The Sisyphus table uses a simple polar coordinate system, with the angle (theta) in radians and the distance from center (rho) normalized so that 1.0 is the maximum radius on any size Sisyphus table.  One thing that may surprise you is that movements in positive theta (that is, the ending point has a more positive theta than the starting point) are *clockwise*, not anti-clockwise.  In addition, in *JSisyphus* the zero theta radial is coincident with the positive Y axis of the equivalent Cartesian coordinate system.

To make shapes like straight lines easy to create, *JSisyphus* maintains positions on the table as both Cartesian coordinates and as polar coordinates.  One challenge with this is that to the Sisyphus table, the theta values 2pi and 4pi are *not* equivalent.  For instance, a line specified in polar coordinates that starts at (0.5, 2pi) and ends at (0.5, 4pi) is a full circle to the Sisyphus table, not just a point.  But conventional translation of those two points to Cartesian coordinates would have both of them as (0, .5).  To handle this in *JSisyphus*, we've added a third element to Cartesian coordinates: "turns".  Each "turn" represents a complete revolution around the origin, and essentially makes the Cartesian coordinates 3D instead of 2D.  *JSisyphus* uses Cartesian coordinates of any value, but only those with (x, y) in ([-1..1],[-1..1]) where sqrt(x^2 + y^2) < 1 can be traced on the Sisyphus table.  Turn 0 includes the angles (-pi..pi), turn 1 (pi..3pi), turn 2 (3pi..5pi) and so on.  Negative turns work exactly the same way, with negative angles.  Note that angles that are exactly at any odd multiple of pi have an ambiguous number of turns, as they could be included in either of the adjacent turns.  The two polar points above ((0.5, 2pi), (0.5, 4pi))would be represented as (0, .5, 1) and (0, .5, 2), thus preserving the information in the original polar form.

Most of *JSisyphus* uses "Sisyphus" units for distance, where 1.0 is the radius of the table.  Utilities are provided for converting those to metric units if needed, as well as the other way around.

### Motion of the ball on a Sisyphus table
If you're used to working with graphics in a Cartesian coordinate system (as the author was!), the low-level workings of the Sisyphus table are a bit of a mind-bender.  The .thr file that defines a track to the Sisyphus table consists of a series of vertices defined as (theta, rho) pairs in plain text.  There's one pair to a text line, and a space separates theta and rho.  The table may play the track either forwards or backwards (that is, from the first vertice to the last, or the other way around).  To move from one vertice to another, the table always moves with a constant delta theta and delta rho for that line (the rates are computed for each move).  The result is a segment of an arithmetic spiral, moving clockwise if the starting theta is less than the ending theta, anti-clockwise if the starting theta is greater than the ending theta, and radially if the starting theta equals the ending theta.  Theta need not be within the range -pi..pi.  For instance, the vertice pair (in rho, theta form) 0,0 .. 1,200pi will make the Sisyphus table draw a beautiful arithmetic spiral with 100 turns, from the center to the outside edge.  This is, in fact, how the table does an "erase" operation.

An additional novelty of tracing lines on the Sisyphus table is that the trace must be a single contiguous line.  That's because the table has no mechanism for picking up the ball, moving to another location, and putting the ball back down again.  Whatever design you're making *must* be traced in its entirety as a single line.  You can, however, trace the *same* line (or line segment) repeatedly, and this capability can often simulate a movement with the ball up.

If you're making a track with any shape *other* than an arithmetic spiral, you must do so by drawing a series of arithmetic spiral segments whose error relative to the track you desire is within acceptable limits.  This is very similar to the way typical graphics systems in Cartesian coordinates draw arbitrary shapes by drawing a series of short straight lines.  It's the same idea, but ... the math is largely different, and in some ways it's more challenging in a polar coordinate system.  Dealing with all these issues is the bulk of what *JSisyphus* does.