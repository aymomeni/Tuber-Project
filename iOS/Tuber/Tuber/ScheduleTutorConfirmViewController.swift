//
//  ScheduleTutorConfirmViewController.swift
//  Tuber
//
//  Created by Anne on 12/4/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit
import Parse

class ScheduleTutorConfirmViewController: UIViewController {

    @IBOutlet weak var confirmLabel: UILabel!
    var passed: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = ClassListViewController.selectedClass.className

        confirmLabel.text = passed
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func confirmButtonPressed(_ sender: Any) {
        
        //Add request to database
        
        performSegue(withIdentifier: "scheduleConfirmed", sender: "hi")
        
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
