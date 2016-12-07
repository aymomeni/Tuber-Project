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
    
    var passed: String!
    
    var icons = [#imageLiteral(resourceName: "tutorservices"), #imageLiteral(resourceName: "studyhotspot"), #imageLiteral(resourceName: "discussion"), #imageLiteral(resourceName: "messaging"), #imageLiteral(resourceName: "offertutor")]
    var names = ["Tutor Services", "Study Hotspot", "Discussion Forum", "Messaging", "Offer To Tutor"]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        //Class name in nav bar
//        print(passed)
        self.title = passed
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
        
        //        let toPass = currentCell.textLabel!.text
        let toPass = currentCell.optionNameLabel.text
        
        if toPass == "Tutor Services"
        {
            performSegue(withIdentifier: "tutorServices", sender: toPass)
            
        }
        
    }
    

}
