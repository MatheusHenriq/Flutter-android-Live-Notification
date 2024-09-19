import 'dart:async';

import 'package:android_live_notification/src/model/live_notification_model.dart';
import 'package:android_live_notification/src/services/live_notification_service.dart';
import 'package:flutter/material.dart';

class HomeView extends StatefulWidget {
  const HomeView({super.key});

  @override
  State<HomeView> createState() => _HomeViewState();
}

class _HomeViewState extends State<HomeView> {
  Timer? timer;
  LiveNotificationService liveNotificationService = LiveNotificationService();
  LiveNotificationModel liveNotificationData = LiveNotificationModel(minutesToDelivery: 1, progress: 0);

  startPooling() {
    timer?.cancel();
    int seconds = 0;
    int minutes = liveNotificationData.minutesToDelivery;
    int progress = 0;
    timer = Timer.periodic(const Duration(seconds: 1), (value) async {
      setState(() {
        seconds++;
        if (seconds % 60 == 0) {
          liveNotificationData.minutesToDelivery--;
        }
        progress = ((seconds * 100) / (minutes * 60)).round();
      });
      if (liveNotificationData.progress >= 98) {
        await liveNotificationService.finishDeliveryNotification().then((value) {
          timer?.cancel();
        });
      } else {
        liveNotificationData.progress = progress;
        await liveNotificationService.updateNotifications(data: liveNotificationData);
      }
    });
  }

  endPooling() {
    timer?.cancel();
    liveNotificationData = LiveNotificationModel(minutesToDelivery: 1, progress: 0);
    liveNotificationService.endNotifications();
  }

  @override
  void dispose() {
    endPooling();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: const Text("Live notification"),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Center(
            child: ElevatedButton(
              onPressed: () async {
                setState(() {
                  liveNotificationData = LiveNotificationModel(minutesToDelivery: 1, progress: 0);
                });
                await liveNotificationService.startNotifications(data: liveNotificationData).then((value) {
                  startPooling();
                });
              },
              child: const Text("Start Notifications"),
            ),
          ),
          const SizedBox(
            height: 16,
          ),
          ElevatedButton(
            onPressed: () {
              endPooling();
            },
            child: const Text("End Notifications"),
          )
        ],
      ),
    );
  }
}
