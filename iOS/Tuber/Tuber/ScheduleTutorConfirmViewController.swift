//
//  ScheduleTutorConfirmViewController.swift
//  Tuber
//
//  Created by Anne on 12/4/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class ScheduleTutorConfirmViewController: UIViewController {

    @IBOutlet weak var confirmLabel: UILabel!
    var passed: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = ClassListViewController.selectedClass.className

        confirmLabel.text = passed
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func confirmButtonPressed(_ sender: Any) {
        
        //TODO: Add request to database
        
        performSegue(withIdentifier: "scheduleConfirmed", sender: "hi")
        
    }


}
