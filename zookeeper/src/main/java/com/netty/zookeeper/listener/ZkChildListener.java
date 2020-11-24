package org.liveme.zookeeper.listener;

import java.util.List;

import com.github.zkclient.IZkChildListener;

/**
 * 暂时不需要这个类，因为目录是不加密的
 * 
 * @author zhuzhan
 *
 */
public class ZkChildListener implements IZkChildListener {
  private ZkChildrenCallback callback;

  public ZkChildListener(ZkChildrenCallback callback) {
    this.callback = callback;
  }

  @Override
  public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
    if (currentChildren != null && currentChildren.size() > 0) {
      callback.handleChildChange(parentPath, currentChildren);
    }
  }

}

interface ZkChildrenCallback {
  void handleChildChange(String parentPath, List<String> currentChildren);
}
