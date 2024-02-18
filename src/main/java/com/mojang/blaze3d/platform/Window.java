package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Locale.Category;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public final class Window implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GLFWErrorCallback defaultErrorCallback = GLFWErrorCallback.create(this::defaultErrorCallback);
   private final WindowEventHandler eventHandler;
   private final ScreenManager screenManager;
   private final long window;
   private int windowedX;
   private int windowedY;
   private int windowedWidth;
   private int windowedHeight;
   private Optional<VideoMode> preferredFullscreenVideoMode;
   private boolean fullscreen;
   private boolean actuallyFullscreen;
   private int x;
   private int y;
   public static int width;
   public int height;
   private int framebufferWidth;
   private int framebufferHeight;
   private int guiScaledWidth;
   private int guiScaledHeight;
   private double guiScale;
   private String errorSection = "";
   private boolean dirty;
   private int framerateLimit;
   private boolean vsync;

   public Window(WindowEventHandler p_85372_, ScreenManager p_85373_, DisplayData displayData, @Nullable String p_85375_, String p_85376_) {
      RenderSystem.assertInInitPhase();
      this.screenManager = p_85373_;
      this.setBootErrorCallback();
      this.setErrorSection("Pre startup");
      this.eventHandler = p_85372_;
      Optional<VideoMode> optional = VideoMode.read(p_85375_);
      if (optional.isPresent()) {
         this.preferredFullscreenVideoMode = optional;
      } else if (displayData.fullscreenWidth.isPresent() && displayData.fullscreenHeight.isPresent()) {
         this.preferredFullscreenVideoMode = Optional.of(new VideoMode(displayData.fullscreenWidth.getAsInt(), displayData.fullscreenHeight.getAsInt(), 8, 8, 8, 60));
      } else {
         this.preferredFullscreenVideoMode = Optional.empty();
      }

      this.actuallyFullscreen = this.fullscreen = displayData.isFullscreen;
      Monitor monitor = p_85373_.getMonitor(GLFW.glfwGetPrimaryMonitor());
      this.windowedWidth = this.width = displayData.width > 0 ? displayData.width : 1;
      this.windowedHeight = this.height = displayData.height > 0 ? displayData.height : 1;
      GLFW.glfwDefaultWindowHints();
      GLFW.glfwWindowHint(139265, 196609);
      GLFW.glfwWindowHint(139275, 221185);
      GLFW.glfwWindowHint(139266, 3);
      GLFW.glfwWindowHint(139267, 2);
      GLFW.glfwWindowHint(139272, 204801);
      GLFW.glfwWindowHint(139270, 1);
      this.window = GLFW.glfwCreateWindow(this.width, this.height, p_85376_, this.fullscreen && monitor != null ? monitor.getMonitor() : 0L, 0L);
      if (monitor != null) {
         VideoMode videomode = monitor.getPreferredVidMode(this.fullscreen ? this.preferredFullscreenVideoMode : Optional.empty());
         this.windowedX = this.x = monitor.getX() + videomode.getWidth() / 2 - this.width / 2;
         this.windowedY = this.y = monitor.getY() + videomode.getHeight() / 2 - this.height / 2;
      } else {
         int[] aint1 = new int[1];
         int[] aint = new int[1];
         GLFW.glfwGetWindowPos(this.window, aint1, aint);
         this.windowedX = this.x = aint1[0];
         this.windowedY = this.y = aint[0];
      }

      GLFW.glfwMakeContextCurrent(this.window);
      Locale locale = Locale.getDefault(Category.FORMAT);
      Locale.setDefault(Category.FORMAT, Locale.ROOT);
      GL.createCapabilities();
      Locale.setDefault(Category.FORMAT, locale);
      this.setMode();
      this.refreshFramebufferSize();
      GLFW.glfwSetFramebufferSizeCallback(this.window, this::onFramebufferResize);
      GLFW.glfwSetWindowPosCallback(this.window, this::onMove);
      GLFW.glfwSetWindowSizeCallback(this.window, this::onResize);
      GLFW.glfwSetWindowFocusCallback(this.window, this::onFocus);
      GLFW.glfwSetCursorEnterCallback(this.window, this::onEnter);
   }

   public int getRefreshRate() {
      RenderSystem.assertOnRenderThread();
      return GLX._getRefreshRate(this);
   }

   public boolean shouldClose() {
      return GLX._shouldClose(this);
   }

   public static void checkGlfwError(BiConsumer<Integer, String> p_85408_) {
      RenderSystem.assertInInitPhase();

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
         int i = GLFW.glfwGetError(pointerbuffer);
         if (i != 0) {
            long j = pointerbuffer.get();
            String s = j == 0L ? "" : MemoryUtil.memUTF8(j);
            p_85408_.accept(i, s);
         }
      }

   }

   public void setIcon(PackResources p_281860_, IconSet p_282155_) throws IOException {
      RenderSystem.assertInInitPhase();
      if (Minecraft.ON_OSX) {
         MacosUtil.loadIcon(p_282155_.getMacIcon(p_281860_));
      } else {
         List<IoSupplier<InputStream>> list = p_282155_.getStandardIcons(p_281860_);
         List<ByteBuffer> list1 = new ArrayList<>(list.size());

         try (MemoryStack memorystack = MemoryStack.stackPush()) {
            GLFWImage.Buffer buffer = GLFWImage.malloc(list.size(), memorystack);

            for(int i = 0; i < list.size(); ++i) {
               try (NativeImage nativeimage = NativeImage.read(list.get(i).get())) {
                  ByteBuffer bytebuffer = MemoryUtil.memAlloc(nativeimage.getWidth() * nativeimage.getHeight() * 4);
                  list1.add(bytebuffer);
                  bytebuffer.asIntBuffer().put(nativeimage.getPixelsRGBA());
                  buffer.position(i);
                  buffer.width(nativeimage.getWidth());
                  buffer.height(nativeimage.getHeight());
                  buffer.pixels(bytebuffer);
               }
            }

            GLFW.glfwSetWindowIcon(this.window, buffer.position(0));
         } finally {
            list1.forEach(MemoryUtil::memFree);
         }

      }
   }

   public void setErrorSection(String p_85404_) {
      this.errorSection = p_85404_;
   }

   private void setBootErrorCallback() {
      RenderSystem.assertInInitPhase();
      GLFW.glfwSetErrorCallback(Window::bootCrash);
   }

   private static void bootCrash(int p_85413_, long p_85414_) {
      RenderSystem.assertInInitPhase();
      String s = "GLFW error " + p_85413_ + ": " + MemoryUtil.memUTF8(p_85414_);
      TinyFileDialogs.tinyfd_messageBox("Minecraft", s + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).", "ok", "error", false);
      throw new Window.WindowInitFailed(s);
   }

   public void defaultErrorCallback(int p_85383_, long p_85384_) {
      RenderSystem.assertOnRenderThread();
      String s = MemoryUtil.memUTF8(p_85384_);
      LOGGER.error("########## GL ERROR ##########");
      LOGGER.error("@ {}", (Object)this.errorSection);
      LOGGER.error("{}: {}", p_85383_, s);
   }

   public void setDefaultErrorCallback() {
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback(this.defaultErrorCallback);
      if (glfwerrorcallback != null) {
         glfwerrorcallback.free();
      }

   }

   public void updateVsync(boolean p_85410_) {
      RenderSystem.assertOnRenderThreadOrInit();
      this.vsync = p_85410_;
      GLFW.glfwSwapInterval(p_85410_ ? 1 : 0);
   }

   public void close() {
      RenderSystem.assertOnRenderThread();
      Callbacks.glfwFreeCallbacks(this.window);
      this.defaultErrorCallback.close();
      GLFW.glfwDestroyWindow(this.window);
      GLFW.glfwTerminate();
   }

   private void onMove(long p_85389_, int p_85390_, int p_85391_) {
      this.x = p_85390_;
      this.y = p_85391_;
   }

   private void onFramebufferResize(long p_85416_, int p_85417_, int p_85418_) {
      if (p_85416_ == this.window) {
         int i = this.getWidth();
         int j = this.getHeight();
         if (p_85417_ != 0 && p_85418_ != 0) {
            this.framebufferWidth = p_85417_;
            this.framebufferHeight = p_85418_;
            if (this.getWidth() != i || this.getHeight() != j) {
               this.eventHandler.resizeDisplay();
            }

         }
      }
   }

   private void refreshFramebufferSize() {
      RenderSystem.assertInInitPhase();
      int[] aint = new int[1];
      int[] aint1 = new int[1];
      GLFW.glfwGetFramebufferSize(this.window, aint, aint1);
      this.framebufferWidth = aint[0] > 0 ? aint[0] : 1;
      this.framebufferHeight = aint1[0] > 0 ? aint1[0] : 1;
   }

   private void onResize(long p_85428_, int p_85429_, int p_85430_) {
      this.width = p_85429_;
      this.height = p_85430_;
   }

   private void onFocus(long p_85393_, boolean p_85394_) {
      if (p_85393_ == this.window) {
         this.eventHandler.setWindowActive(p_85394_);
      }

   }

   private void onEnter(long p_85420_, boolean p_85421_) {
      if (p_85421_) {
         this.eventHandler.cursorEntered();
      }

   }

   public void setFramerateLimit(int p_85381_) {
      this.framerateLimit = p_85381_;
   }

   public int getFramerateLimit() {
      return this.framerateLimit;
   }

   public void updateDisplay() {
      RenderSystem.flipFrame(this.window);
      if (this.fullscreen != this.actuallyFullscreen) {
         this.actuallyFullscreen = this.fullscreen;
         this.updateFullscreen(this.vsync);
      }

   }

   public Optional<VideoMode> getPreferredFullscreenVideoMode() {
      return this.preferredFullscreenVideoMode;
   }

   public void setPreferredFullscreenVideoMode(Optional<VideoMode> p_85406_) {
      boolean flag = !p_85406_.equals(this.preferredFullscreenVideoMode);
      this.preferredFullscreenVideoMode = p_85406_;
      if (flag) {
         this.dirty = true;
      }

   }

   public void changeFullscreenVideoMode() {
      if (this.fullscreen && this.dirty) {
         this.dirty = false;
         this.setMode();
         this.eventHandler.resizeDisplay();
      }

   }

   private void setMode() {
      RenderSystem.assertInInitPhase();
      boolean flag = GLFW.glfwGetWindowMonitor(this.window) != 0L;
      if (this.fullscreen) {
         Monitor monitor = this.screenManager.findBestMonitor(this);
         if (monitor == null) {
            LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
            this.fullscreen = false;
         } else {
            if (Minecraft.ON_OSX) {
               MacosUtil.toggleFullscreen(this.window);
            }

            VideoMode videomode = monitor.getPreferredVidMode(this.preferredFullscreenVideoMode);
            if (!flag) {
               this.windowedX = this.x;
               this.windowedY = this.y;
               this.windowedWidth = this.width;
               this.windowedHeight = this.height;
            }

            this.x = 0;
            this.y = 0;
            this.width = videomode.getWidth();
            this.height = videomode.getHeight();
            GLFW.glfwSetWindowMonitor(this.window, monitor.getMonitor(), this.x, this.y, this.width, this.height, videomode.getRefreshRate());
         }
      } else {
         this.x = this.windowedX;
         this.y = this.windowedY;
         this.width = this.windowedWidth;
         this.height = this.windowedHeight;
         GLFW.glfwSetWindowMonitor(this.window, 0L, this.x, this.y, this.width, this.height, -1);
      }

   }

   public void toggleFullScreen() {
      this.fullscreen = !this.fullscreen;
   }

   public void setWindowed(int p_166448_, int p_166449_) {
      this.windowedWidth = p_166448_;
      this.windowedHeight = p_166449_;
      this.fullscreen = false;
      this.setMode();
   }

   private void updateFullscreen(boolean p_85432_) {
      RenderSystem.assertOnRenderThread();

      try {
         this.setMode();
         this.eventHandler.resizeDisplay();
         this.updateVsync(p_85432_);
         this.updateDisplay();
      } catch (Exception exception) {
         LOGGER.error("Couldn't toggle fullscreen", (Throwable)exception);
      }

   }

   public int calculateScale(int p_85386_, boolean p_85387_) {
      int i;
      for(i = 1; i != p_85386_
              && i < this.framebufferWidth
              && i < this.framebufferHeight
              && this.framebufferWidth / (i + 1) >= 320
              && this.framebufferHeight / (i + 1) >= 280; ++i) {
      }

      if (p_85387_ && i % 2 != 0) {
         ++i;
      }

      return i;
   }

   public void setGuiScale(double p_85379_) {
      this.guiScale = p_85379_;
      int i = (int)((double)this.framebufferWidth / p_85379_);
      this.guiScaledWidth = (double)this.framebufferWidth / p_85379_ > (double)i ? i + 1 : i;
      int j = (int)((double)this.framebufferHeight / p_85379_);
      this.guiScaledHeight = (double)this.framebufferHeight / p_85379_ > (double)j ? j + 1 : j;
   }

   public void setTitle(String p_85423_) {
      GLFW.glfwSetWindowTitle(this.window, p_85423_);
   }

   public long getWindow() {
      return this.window;
   }

   public boolean isFullscreen() {
      return this.fullscreen;
   }

   public int getWidth() {
      return this.framebufferWidth;
   }

   public int getHeight() {
      return this.framebufferHeight;
   }

   public void setWidth(int p_166451_) {
      this.framebufferWidth = p_166451_;
   }

   public void setHeight(int p_166453_) {
      this.framebufferHeight = p_166453_;
   }

   public int getScreenWidth() {
      return this.width;
   }

   public int getScreenHeight() {
      return this.height;
   }

   public int getGuiScaledWidth() {
      return this.guiScaledWidth;
   }

   public int getGuiScaledHeight() {
      return this.guiScaledHeight;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public double getGuiScale() {
      return this.guiScale;
   }

   @Nullable
   public Monitor findBestMonitor() {
      return this.screenManager.findBestMonitor(this);
   }

   public void updateRawMouseInput(boolean p_85425_) {
      InputConstants.updateRawMouseInput(this.window, p_85425_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class WindowInitFailed extends SilentInitException {
      WindowInitFailed(String p_85455_) {
         super(p_85455_);
      }
   }
}