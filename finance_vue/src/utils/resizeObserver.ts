/**
 * 节流函数
 * @param fn 要执行的函数
 * @param delay 延迟时间（毫秒）
 */
function throttle<T extends (...args: any[]) => any>(fn: T, delay: number): (...args: Parameters<T>) => void {
  let lastTime = 0;
  let frameId: number | null = null;
  
  return function (this: any, ...args: Parameters<T>): void {
    const now = Date.now();
    
    if (now - lastTime >= delay) {
      if (frameId) {
        cancelAnimationFrame(frameId);
      }
      
      frameId = requestAnimationFrame(() => {
        fn.apply(this, args);
        lastTime = now;
        frameId = null;
      });
    }
  };
}

/**
 * 创建优化的 ResizeObserver
 * @param callback 回调函数
 * @param delay 节流延迟时间（毫秒）
 */
export function createDebouncedResizeObserver(
  callback: ResizeObserverCallback,
  delay: number = 100
): ResizeObserver {
  const throttledCallback = throttle(callback, delay);
  return new ResizeObserver(throttledCallback);
}

/**
 * 安装全局错误处理器来忽略特定的 ResizeObserver 错误
 * 这个函数应该在应用初始化时调用一次
 */
export function setupResizeObserverErrorHandler(): void {
  // 全局错误处理
  window.addEventListener('error', (e) => {
    if (e.message === 'ResizeObserver loop completed with undelivered notifications.' ||
        e.message === 'ResizeObserver loop limit exceeded') {
      e.stopImmediatePropagation();
      e.preventDefault();
      return false;
    }
    return true;
  }, true);
}

/**
 * 使用示例：
 * 
 * import { createDebouncedResizeObserver } from '@/utils/resizeObserver';
 * 
 * const observer = createDebouncedResizeObserver((entries) => {
 *   entries.forEach(entry => {
 *     // 处理 resize 事件
 *   });
 * });
 * 
 * // 开始观察元素
 * observer.observe(element);
 * 
 * // 停止观察
 * observer.disconnect();
 */ 