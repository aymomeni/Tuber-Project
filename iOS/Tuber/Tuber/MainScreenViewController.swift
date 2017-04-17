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
        self.navigationController?.navigationBar.barTintColor = UIColor(red: 30.0/255.0, green: 30.0/255.0, blue: 30.0/255.0, alpha: 1.0)
        self.navigationController?.navigationBar.shadowImage = UIImage()
        self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: UIBarMetrics.default)
//        self.navigationController?.navigationBar.barStyle = UIBarStyle.black
        self.navigationController?.navigationBar.tintColor = UIColor.black //sidebuttons
        self.navigationController?.navigationBar.titleTextAttributes = [NSForegroundColorAttributeName: UIColor.orange]
        
//        self.navigationItem.leftBarButtonItem = UIBarButtonItem(title: "<-", style: UIBarButtonItemStyle.done, target: self, action: #selector(MainScreenViewController.didTapGoToLeft))
//        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "->", style: UIBarButtonItemStyle.done, target: self, action: #selector(MainScreenViewController.didTapGoToRight))
        
        // MARK: - Scroll menu setup
        
        // Initialize view controllers to display and place in array
        var controllerArray : [UIViewController] = []
        
        //        let controller1 : ClassListViewController = ClassListViewController(nibName: "ClassListViewController", bundle: nil)
        //        let controller1 : ClassListViewController = ClassListViewController()
        let controller1 = storyboard?.instantiateViewController(withIdentifier: "ClassList")
        controller1?.title = "STUDENT"
        controllerArray.append(controller1!)
        let controller2 : ClassListViewController = ClassListViewController()//(nibName: "ClassListViewController", bundle: nil)
        controller2.title = "TUTOR"
        controllerArray.append(controller2)
        
        // Customize menu (Optional)
        let parameters: [CAPSPageMenuOption] = [
            .scrollMenuBackgroundColor(UIColor.white), //color of scrollmenu
//            .scrollMenuBackgroundColor(UIColor(red: 30.0/255.0, green: 30.0/255.0, blue: 30.0/255.0, alpha: 1.0)),
            .viewBackgroundColor(UIColor.white), //color of extra backgroud of the views
//            .viewBackgroundColor(UIColor(red: 20.0/255.0, green: 20.0/255.0, blue: 20.0/255.0, alpha: 1.0)),
            .selectionIndicatorColor(UIColor.orange),
            .bottomMenuHairlineColor(UIColor.white), //separation between page menu and view
//            .bottomMenuHairlineColor(UIColor(red: 70.0/255.0, green: 70.0/255.0, blue: 80.0/255.0, alpha: 1.0)),
            .menuItemFont(UIFont(name: "HelveticaNeue", size: 13.0)!),
            .menuHeight(40.0),
            .menuItemWidth(90.0),
            .centerMenuItems(true),
            .addBottomMenuShadow(true),
            .selectedMenuItemLabelColor(UIColor.blue),
//            .menuShadowColor(UIColor.blue),
            .menuShadowRadius(4)
        ]
        
        // Initialize scroll menu
        let navheight = (navigationController?.navigationBar.frame.size.height ?? 0) + UIApplication.shared.statusBarFrame.size.height
        
        let rect = CGRect(origin: CGPoint(x: 0,y :navheight), size: CGSize(width: self.view.frame.width, height: self.view.frame.height - navheight))
        pageMenu = CAPSPageMenu(viewControllers: controllerArray, frame: rect, pageMenuOptions: parameters)
        
        
        
//        let frame = CGRectMake(0, navheight, view.frame.width, view.frame.height-navheight)
//        pageMenu = CAPSPageMenu(viewControllers: controllerArray, frame: frame, pageMenuOptions: menuParameters)
//        self.view.addSubview(pageMenu!.view)
        
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

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
