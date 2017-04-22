//
//  MainScreenViewController.swift
//  Tuber
//
//  Created by Anne on 4/15/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class MainScreenViewController: UIViewController {
    
    var pageMenu : CAPSPageMenu?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        self.title = "TUBER"
        //        self.navigationController?.navigationBar.barTintColor = UIColor(red: 30.0/255.0, green: 30.0/255.0, blue: 30.0/255.0, alpha: 1.0)
        self.navigationController?.navigationBar.barTintColor = UIColor.darkGray
        self.navigationController?.navigationBar.shadowImage = UIImage()
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: UIBarMetrics.default)
        self.navigationController?.navigationBar.barStyle = UIBarStyle.default
        self.navigationController?.navigationBar.tintColor = UIColor.black //sidebuttons
        self.navigationController?.navigationBar.titleTextAttributes = [NSForegroundColorAttributeName: UIColor.orange]
        //        self.navigationController?.navigationBar.isTranslucent = false
        
        //        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: UIBarMetrics.default)
        //        self.navigationController?.navigationBar.shadowImage = UIImage()
        self.navigationController?.navigationBar.isTranslucent = true
        self.navigationController?.view.backgroundColor = UIColor.clear
        //        self.edgesForExtendedLayout = []
        
        
        // MARK: - Scroll menu setup
        
        // Initialize view controllers to display and place in array
        var controllerArray : [UIViewController] = []
        
        let controller1 = storyboard?.instantiateViewController(withIdentifier: "ClassList")
        controller1?.title = "STUDENT CLASSES"
        controllerArray.append(controller1!)
        
        let controller2 = storyboard?.instantiateViewController(withIdentifier: "TutorClassList")
        controller2?.title = "TUTOR CLASSES"
        controllerArray.append(controller2!)
        
        // Customize menu (Optional)
        let parameters: [CAPSPageMenuOption] = [
            .scrollMenuBackgroundColor(UIColor.gray), //color of scrollmenu
            .viewBackgroundColor(UIColor.black), //color of extra backgroud of the views
            .selectionIndicatorColor(UIColor.orange),
            .bottomMenuHairlineColor(UIColor.lightGray), //separation between page menu and view
            .menuItemFont(UIFont(name: "HelveticaNeue", size: 13.0)!),
            .menuHeight(40.0),
            .menuItemWidth(130.0),
            .centerMenuItems(true),
            .addBottomMenuShadow(true),
            .selectedMenuItemLabelColor(UIColor.white),
            //            .menuShadowColor(UIColor.blue),
            .menuShadowRadius(4)
        ]
        
        // Initialize scroll menu
        let navheight = (navigationController?.navigationBar.frame.size.height ?? 0) + UIApplication.shared.statusBarFrame.size.height
        let rect = CGRect(origin: CGPoint(x: 0,y :navheight), size: CGSize(width: self.view.frame.width, height: self.view.frame.height - navheight))
        pageMenu = CAPSPageMenu(viewControllers: controllerArray, frame: rect, pageMenuOptions: parameters)
        
        self.addChildViewController(pageMenu!)
        self.view.addSubview(pageMenu!.view)
        
        pageMenu!.didMove(toParentViewController: self)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func didTapGoToLeft() {
        let currentIndex = pageMenu!.currentPageIndex
        
        if currentIndex > 0 {
            pageMenu!.moveToPage(currentIndex - 1)
        }
    }
    
    func didTapGoToRight() {
        let currentIndex = pageMenu!.currentPageIndex
        
        if currentIndex < pageMenu!.controllerArray.count {
            pageMenu!.moveToPage(currentIndex + 1)
        }
    }
    
    override func shouldAutomaticallyForwardRotationMethods() -> Bool {
        return true
    }
    
}

