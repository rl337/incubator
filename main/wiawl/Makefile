CC = g++
CCFLAGS = -Wall -c -g
#CCFLAGS = -Wall -c
SRCDIR = src
CONFDIR = conf
BUILDDIR = build
LINKFLAGS = -framework SDL -framework SDL_image -framework Cocoa -Wl,-rpath,@loader_path/../Frameworks
RESOURCEDIR = Resources
PLATFORM = osx


EXENAME = wiawl
BUNDLEDIR = $(BUILDDIR)/$(EXENAME).app

OSXTARGETS = $(BUILDDIR)/SDLMain.o

SDL_FRAMEWORKDIR=/Library/Frameworks/SDL.framework
SDLIMAGE_FRAMEWORKDIR=/Library/Frameworks/SDL_Image.framework

default : 
	echo "valid targets are: osx clean"

osx : $(BUNDLEDIR)

$(BUILDDIR)/$(EXENAME) : $(BUILDDIR) $(BUILDDIR)/SDLAppMain.o $(BUILDDIR)/Level.o $(BUILDDIR)/main.o $(OSXTARGETS)
	$(CC) -o $(BUILDDIR)/$(EXENAME) $(BUILDDIR)/main.o $(BUILDDIR)/Level.o $(BUILDDIR)/SDLAppMain.o $(LINKFLAGS) $(OSXTARGETS)

$(BUILDDIR) :
	mkdir -p $(BUILDDIR)

$(BUILDDIR)/main.o : $(SRCDIR)/main.cpp
	$(CC) -o $(BUILDDIR)/main.o $(CCFLAGS) $(SRCDIR)/main.cpp

$(BUILDDIR)/SDLMain.o : $(SRCDIR)/osx/SDLMain.m $(SRCDIR)/osx/SDLMain.h
	$(CC) -o $(BUILDDIR)/SDLMain.o $(CCFLAGS) $(SRCDIR)/osx/SDLMain.m

$(BUILDDIR)/Level.o : $(SRCDIR)/Level.h $(SRCDIR)/Level.cpp
	$(CC) -o $(BUILDDIR)/Level.o $(CCFLAGS) $(SRCDIR)/Level.cpp

$(BUILDDIR)/SDLAppMain.o : $(SRCDIR)/SDLAppMain.h $(SRCDIR)/SDLAppMain.cpp $(BUILDDIR)/Level.o
	$(CC) -o $(BUILDDIR)/SDLAppMain.o $(CCFLAGS) $(SRCDIR)/SDLAppMain.cpp

clean : 
	rm -rf $(BUILDDIR)

$(BUNDLEDIR) : $(BUILDDIR)/$(EXENAME)
	mkdir -p $(BUNDLEDIR)
	mkdir -p $(BUNDLEDIR)/Contents
	mkdir -p $(BUNDLEDIR)/Contents/MacOS
	mkdir -p $(BUNDLEDIR)/Contents/Resources
	mkdir -p $(BUNDLEDIR)/Contents/Frameworks
	cp $(CONFDIR)/Info.plist $(BUNDLEDIR)/Contents
	cp $(BUILDDIR)/$(EXENAME) $(BUNDLEDIR)/Contents/MacOS
	mkdir -p $(BUNDLEDIR)/Contents/Frameworks
	cp -a $(RESOURCEDIR) $(BUNDLEDIR)/Contents
	mkdir -p $(BUNDLEDIR)/Contents/Frameworks/SDL.framework/Versions/$(SDL_FRAMEWORK_VERSION)
	mkdir -p $(BUNDLEDIR)/Contents/Frameworks/SDL_Image.framework/Versions/$(SDLIMAGE_FRAMEWORK_VERSION)
	cp -a $(SDL_FRAMEWORKDIR) $(BUNDLEDIR)/Contents/Frameworks
	cp -a $(SDLIMAGE_FRAMEWORKDIR) $(BUNDLEDIR)/Contents/Frameworks
