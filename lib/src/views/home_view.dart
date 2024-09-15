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
  LiveNotificationModel liveNotificationData = LiveNotificationModel(minutesToDelivery: 5, progress: 0);

  startPooling() {
    timer?.cancel();
    int seconds = 0;
    setState(() {
      liveNotificationData = LiveNotificationModel(minutesToDelivery: 5, progress: 0);
    });
    timer = Timer.periodic(const Duration(seconds: 1), (value) {
      setState(() {
        seconds++;
        if (seconds % 60 == 0) {
          liveNotificationData.minutesToDelivery--;
        }
        liveNotificationData.progress = ((seconds * 100) / (5 * 60)).round();
      });
      liveNotificationService.updateNotifications(data: liveNotificationData);
      if (liveNotificationData.minutesToDelivery == 0) {
        endPooling();
      }
    });
  }

  endPooling() {
    timer?.cancel();
    liveNotificationService.endNotifications();
  }

  //METHODS HERE
  void startNotification() {
    startNotification();
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
