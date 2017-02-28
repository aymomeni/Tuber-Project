//
//  ClassOptionsViewController.swift
//  Tuber
//
//  Created by Anne on 12/7/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class ClassOptionsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet weak var optionTableView: UITableView!
    
    //var passed: String!
    
    var icons = [#imageLiteral(resourceName: "tutorservices"), #imageLiteral(resourceName: "studyhotspot"), #imageLiteral(resourceName: "discussion"), #imageLiteral(resourceName: "messaging"), #imageLiteral(resourceName: "offertutor")]
    var names = ["Tutor Services", "Study Hotspot", "Discussion Forum", "Messaging", "Offer To Tutor"]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = ClassListViewController.selectedClass.className
        
        self.navigationItem.hidesBackButton = true
        let newBackButton = UIBarButtonItem(title: "< Courses", style: UIBarButtonItemStyle.plain, target: self, action: #selector(ClassOptionsViewController.back(sender:)))
        self.navigationItem.leftBarButtonItem = newBackButton
    }
    
    func back(sender: UIBarButtonItem) {
        _ = navigationController?.popToRootViewController(animated: true)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 5
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "classOptions", for: indexPath) as! ClassOptionsTableViewCell
        
        cell.optionIconImageView.image = icons[indexPath.row]
        cell.optionNameLabel.text = names[indexPath.row]
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        
        let currentCell = tableView.cellForRow(at: indexPath!)! as! ClassOptionsTableViewCell
        
        let selectedOption = currentCell.optionNameLabel.text
        
        if selectedOption == "Tutor Services"
        {
            performSegue(withIdentifier: "tutorServices", sender: selectedOption)
            
        }
        else if selectedOption == "Study Hotspot"
        {
            performSegue(withIdentifier: "studyHotspot", sender: selectedOption)
        }
        else if selectedOption == "Offer To Tutor"
        {
            performSegue(withIdentifier: "offerToTutor", sender: selectedOption)
        }
        
    }
    

}
