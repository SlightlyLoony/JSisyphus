<h1 align="center"><b>JSisyphus Factoids</b></h1>

This document is a place to record useful information learned about the Sisyphus table.

## Rho values at the end of a track...
We've learned from an authoritative source (Bruce Shapiro) that the rho values for the last entry in a .thr (vertice) file *must* be either 0 or 1, else positioning errors may occur in subsequently played tracks.

## Consistent behavior at the start a track...
When I first started playing uploaded tracks, I had inconsistent behavior at the beginning of the track.  Sometimes the ball would start by spiraling out to (1, 1) even if my first entry didn't ask for that behavior.  I tried modifying JSisyphus to repeat the first entry twice, and since I made that change I have not seen that unasked-for spiraling.

## Consistent behavior at the end of a track...
When I first started playing uploaded tracks, I had inconsistent behavior at the end of a track.  Most of the time when the track reached the end, it would begin playing the track backwards.  This surprised me, but not in a bad way.  Sometimes, however, instead of playing backwards the table would re-home and then start replaying the track with a different starting theta - that I didn't particularly like.  I tried modifying JSisyphus to repeat the last entry twice, just as with the preceding issue.  As with that one, since I made that change I have not seen any inconsistent track end behavior.