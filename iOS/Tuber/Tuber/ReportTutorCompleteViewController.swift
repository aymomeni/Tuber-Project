//
//  ReportTutorCompleteViewController.swift
//  Tuber
//
//  Created by Anne on 4/25/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class ReportTutorCompleteViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        var navArray:Array = (self.navigationController?.viewControllers)!
        navArray.remove(at: navArray.count - 2)
        navArray.remove(at: navArray.count - 2)
        navArray.remove(at: navArray.count - 2)
        self.navigationController?.viewControllers = navArray
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
